package com.prj.key_exhange;

import org.springframework.web.bind.annotation.*;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.util.*;

@RestController
@RequestMapping("/private")
public class EncryptModule {

    private static KeyStore loadKeyStore(String keyStorePath, String passWord) {
        KeyStore keystore = null;
        try {
            keystore = KeyStore.getInstance("PKCS12");
        } catch (KeyStoreException e) {
            System.err.println("keyStoreInstance Error!");
            return null;
        }
        InputStream is = null;
        try {
            is = new FileInputStream(new File(keyStorePath));
        } catch (FileNotFoundException e) {
            System.err.println("KeyStore File not found!");
            return null;
        }
        try {
            keystore.load(is, passWord.toCharArray());
        } catch (Exception e) {
            System.err.println("Could not load keyStore!");
            return null;
        }
        return keystore;
    }
    private static byte[] getPrivateKeyFromKeyStore(String keyStorePath, String passWord) {
        KeyStore keystore = loadKeyStore(keyStorePath,passWord);
        try{
            String alias = keystore.aliases().nextElement();
            PrivateKey key = (PrivateKey)keystore.getKey(alias, passWord.toCharArray());
            return key.getEncoded();
        } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException e) {
            e.printStackTrace();
            return null;
        }
    }
    private static Certificate loadCertFromKeyStore(String keyStorePath, String passWord) {
        KeyStore keystore = loadKeyStore(keyStorePath,passWord);
        try {
            String alias = keystore.aliases().nextElement();
            Certificate certificate =  keystore.getCertificate(alias);
            return certificate;
        } catch (KeyStoreException e) {
            System.err.println("Could not loadCertificate From Keystore");
            return null;
        }

    }
    private static Map<String,Object>[] setEncryptElements(String[] elements) {
        Map<String,Object>[] pairs = new Map[elements.length];
        for(int i = 0 ; i < elements.length;i++) {
            pairs[i] = new HashMap<>();
            pairs[i].put("name",elements[i]);
        }
        return pairs;
    }
    private static void dfs(String s, ArrayList<Map<String,Object>> parent, AES128Util aes) throws GeneralSecurityException {
        int pos = s.indexOf("/");
        if(pos == -1) {
            for(Map<String,Object> child : parent)
                child.put(s,Base64.getEncoder().encodeToString(aes.encrypt((String) child.get(s))));
        } else {
            String token = s.substring(0,pos);
            String ss = s.substring(pos + 1);
            for(Map<String,Object> child : parent) {
                Object childElement = child.get(token);
                if(childElement instanceof List)
                    dfs(ss, (ArrayList<Map<String,Object>>)childElement, aes);
                else {
                    ArrayList<Map<String,Object>> maps = new ArrayList<>();
                    maps.add((Map<String, Object>) childElement);
                    dfs(ss,maps,aes);
                }
            }
        }
    }

    @GetMapping(value = "/getCert")
    public @ResponseBody
    Map<String,String> getKey(
            @RequestHeader(value="Device-id", required = true) String deviceId)
            throws CertificateEncodingException {
        X509Certificate certificate = (X509Certificate) loadCertFromKeyStore("keystore.p12","123456");
        Map<String,String> ret = new HashMap<>();
        ret.put("Cert-base64",new String(Base64.getEncoder().encode(certificate.getEncoded())));
        return ret;
    }

    public static void encrypt(String keystorePath,String passWord,String[] encryptElements,
                                              Map<String,Object> reqParam,Map<String,Object> response) {
        SecureRandom rand = new SecureRandom();
        String sPubKey = new String(loadCertFromKeyStore(keystorePath,passWord).getPublicKey().getEncoded());
        String cPubKey = new String(Base64.getDecoder().decode((String) reqParam.get("pubKey")));
        if(!sPubKey.equals(cPubKey)) {
            PublicKeyIncorrectException e = new PublicKeyIncorrectException("PublicKey Incorrect");
            throw e;
        }
        byte[] cKey = Base64.getDecoder().decode((String) reqParam.get("cKey"));
        byte[] privKey = getPrivateKeyFromKeyStore(keystorePath,passWord);
        try {
            cKey = RSAModule.decryptRSA(privKey,cKey);
        } catch (NoSuchAlgorithmException | NoSuchProviderException | NoSuchPaddingException | InvalidKeyException |
                BadPaddingException | IllegalBlockSizeException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
        System.out.println("cKeyBase64 : " + Base64.getEncoder().encodeToString(cKey));
        byte[] sKey = new byte[16];
        rand.nextBytes(sKey);
        System.out.println("sKeyBase64 : " + Base64.getEncoder().encodeToString(sKey));
        byte[] key = new byte[32];
        System.arraycopy(cKey,0,key,0,16);
        System.arraycopy(sKey,0,key,16,16);
        AES128Util aes = new AES128Util(key);
        System.out.println("keyBase64 : " + Base64.getEncoder().encodeToString(key));
        for(String element : encryptElements) {
            ArrayList<Map<String,Object>> maps = new ArrayList<>();
            maps.add(response);
            try {
                dfs(element,maps,aes);
            } catch (GeneralSecurityException e) {
                e.printStackTrace();
            }
        }
        String ret = null;
        try {
             ret = Base64.getEncoder().encodeToString(RSAModule.encryptRSA(privKey,sKey));
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | NoSuchProviderException | InvalidKeyException |
                BadPaddingException | IllegalBlockSizeException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
        Map<String,Object>[] encryptedElements = EncryptModule.setEncryptElements(encryptElements);

        response.put("encryptedElements",encryptedElements);
        response.put("sKey",ret);
    }

}
