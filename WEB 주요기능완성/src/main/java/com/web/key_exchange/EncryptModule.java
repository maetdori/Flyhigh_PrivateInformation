package com.web.key_exchange;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.web.exception.WebException;

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

@Controller
@RequestMapping("/private")
public class EncryptModule {
	
	private static final Logger logger = LoggerFactory.getLogger(EncryptModule.class);

    private static KeyStore loadKeyStore(String keyStorePath, String passWord) throws WebException {
        KeyStore keystore = null;
        try {
            keystore = KeyStore.getInstance("PKCS12");
        } catch (KeyStoreException e) {
        	WebException ee = new WebException("keyStoreInstance Error!",WebException.ENCM_LOADKEYSTORE_NO_INSTANCE,e);
        	throw ee;
        }
        InputStream is = null;
        try {
            is = new FileInputStream(new File(keyStorePath));
        } catch (FileNotFoundException e) {
        	WebException ee = new WebException("KeyStore File not found!",WebException.ENCM_LOADKEYSTORE_FILENOTFOUND,e);
        	throw ee;
        }
        try {
            keystore.load(is, passWord.toCharArray());
        } catch (Exception e) {
        	WebException ee = new WebException("Could not load keyStore!",WebException.ENCM_LOADKEYSTORE_LOAD,e);
        	throw ee;
        }
        return keystore;
    }
    private static byte[] getPrivateKeyFromKeyStore(String keyStorePath, String passWord) throws WebException {
        KeyStore keystore = loadKeyStore(keyStorePath,passWord);
        try {
            String alias = keystore.aliases().nextElement();
            PrivateKey key = (PrivateKey)keystore.getKey(alias, passWord.toCharArray());
            return key.getEncoded();
        } catch (KeyStoreException e) {
        	throw new WebException("Keystore not initialized",WebException.ENCM_GET_PRIVKEY_FROM_KS_KS_NO_INIT ,e);
        } catch (UnrecoverableKeyException e) {
			throw new WebException("Wrong password",WebException.ENCM_GET_PRIVKEY_FROM_KS_WRONGPW ,e);
		} catch (NoSuchAlgorithmException e) {
			throw new WebException("Cannot reslove password Algorithm",WebException.ENCM_GET_PRIVKEY_FROM_KS_UNKNOWNALG ,e);
		}
    }
    private static Certificate loadCertFromKeyStore(String keyStorePath, String passWord) throws WebException {
        KeyStore keystore = loadKeyStore(keyStorePath,passWord);
        try {
            String alias = keystore.aliases().nextElement();
            Certificate certificate =  keystore.getCertificate(alias);
            return certificate;
        } catch (KeyStoreException e) {
        	throw new WebException("Keystore not Initialized",WebException.ENCM_LD_CERT_FROM_KS_KS_NO_INIT,e);
        }

    }
      private static Map<String,Object>[] setEncryptElements(String[] elements) throws WebException {
        Map<String,Object>[] pairs = new Map[elements.length];
        for(int i = 0 ; i < elements.length;i++) {
            pairs[i] = new HashMap<>();
            pairs[i].put("name",elements[i]);
        }
        return pairs;
    }
    private static void dfs(String s, ArrayList<Map<String,Object>> parent, AES128Util aes) throws WebException {
        int pos = s.indexOf("/");
        if(pos == -1) {
            for(Map<String,Object> child : parent) {
            	logger.info("s : " + s);
            	logger.info("child.get(s) : " + child.get(s));
                try {
					child.put(s,Base64.getEncoder().encodeToString(aes.encrypt((String) child.get(s))));
				} catch (GeneralSecurityException e) {
					throw new WebException("Failed Encrypt data (" +child.get(s) + ")" , WebException.ENCM_DFS_AES_ERROR,e);
				}
            }
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
    Map<String,String> getKey(@RequestHeader(value="Device-id", required = true) String deviceId) throws WebException {
        X509Certificate certificate = (X509Certificate) loadCertFromKeyStore("keystore2.p12","123456");
        Map<String,String> ret = new HashMap<>();
        try {
			ret.put("cert_base64",new String(Base64.getEncoder().encode(certificate.getEncoded())));
		} catch (CertificateEncodingException e) {
			throw new WebException("Failed get encoded certificate", WebException.ENCM_GETKEY_CERT_ENCODING_ERROR,e);
		}
        return ret;
    }
    public static void encrypt(String keystorePath,String passWord,String[] encryptElements,
                                              Map<String,Object> reqParam,Map<String,Object> response) throws WebException {
        SecureRandom rand = new SecureRandom();
        String sPubKey = new String(loadCertFromKeyStore(keystorePath,passWord).getPublicKey().getEncoded());
        
        String cPubKey = new String(Base64.getDecoder().decode((String) reqParam.get("pubKey")));
        if(!sPubKey.equals(cPubKey)) {
        	logger.error("sPubkey : " + sPubKey);
        	logger.error("cPubkey : " + cPubKey);
            WebException e = new WebException("PublicKey Incorrect",WebException.ENCM_ENCRYPT_PUBKEY_INCORRECT);
            throw e;
        }
        byte[] cKey = Base64.getDecoder().decode((String) reqParam.get("cKey"));
        byte[] privKey = getPrivateKeyFromKeyStore(keystorePath,passWord);
        cKey = RSAModule.decryptRSA(privKey,cKey);
        logger.info("cKeyBase64 : " + Base64.getEncoder().encodeToString(cKey));
        byte[] sKey = new byte[16];
        rand.nextBytes(sKey);
        logger.info("sKeyBase64 : " + Base64.getEncoder().encodeToString(sKey));
        byte[] key = new byte[32];
        System.arraycopy(cKey,0,key,0,16);
        System.arraycopy(sKey,0,key,16,16);
        AES128Util aes = new AES128Util(key);
        logger.info("keyBase64 : " + Base64.getEncoder().encodeToString(key));
        for(String element : encryptElements) {
            ArrayList<Map<String,Object>> maps = new ArrayList<>();
            maps.add(response);
            dfs(element,maps,aes);
        }
        String ret = Base64.getEncoder().encodeToString(RSAModule.encryptRSA(privKey,sKey));
        Map<String,Object>[] encryptedElements = EncryptModule.setEncryptElements(encryptElements);

        response.put("encryptedElements",encryptedElements);
        response.put("sKey",ret);
    }

}
