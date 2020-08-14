package com.web.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.web.consts.WASJSONConsts;
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


public class ResponseEncryptModule {
	
	private static final Logger logger = LoggerFactory.getLogger(ResponseEncryptModule.class);

    private static KeyStore loadKeyStore(String keyStorePath, String passWord) throws WebException {
        KeyStore keystore = null;
        try {
            keystore = KeyStore.getInstance("PKCS12");
        } catch (KeyStoreException e) {
        	WebException ee = new WebException("keyStoreInstance Error!",WebException.RENCM_LOADKEYSTORE_NO_INSTANCE,e);
        	throw ee;
        }
        InputStream is = null;
        try {
            is = new FileInputStream(new File(keyStorePath));
        } catch (FileNotFoundException e) {
        	WebException ee = new WebException("KeyStore File not found!",WebException.RENCM_LOADKEYSTORE_FILENOTFOUND,e);
        	throw ee;
        }
        try {
            keystore.load(is, passWord.toCharArray());
        } catch (Exception e) {
        	WebException ee = new WebException("Could not load keyStore!",WebException.RENCM_LOADKEYSTORE_LOAD,e);
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
        	throw new WebException("Keystore not initialized",WebException.RENCM_GET_PRIVKEY_FROM_KS_KS_NO_INIT ,e);
        } catch (UnrecoverableKeyException e) {
			throw new WebException("Wrong password",WebException.RENCM_GET_PRIVKEY_FROM_KS_WRONGPW ,e);
		} catch (NoSuchAlgorithmException e) {
			throw new WebException("Cannot reslove password Algorithm",WebException.RENCM_GET_PRIVKEY_FROM_KS_UNKNOWNALG ,e);
		}
    }
    public static Certificate loadCertFromKeyStore(String keyStorePath, String passWord) throws WebException {
        KeyStore keystore = loadKeyStore(keyStorePath,passWord);
        try {
            String alias = keystore.aliases().nextElement();
            Certificate certificate =  keystore.getCertificate(alias);
            return certificate;
        } catch (KeyStoreException e) {
        	throw new WebException("Keystore not Initialized",WebException.RENCM_LD_CERT_FROM_KS_KS_NO_INIT,e);
        }

    }
      private static Map<String,Object>[] setEncryptElements(String[] elements) throws WebException {
        Map<String,Object>[] pairs = new Map[elements.length];
        for(int i = 0 ; i < elements.length;i++) {
            pairs[i] = new HashMap<>();
            pairs[i].put(WASJSONConsts.STRING_ENCNAME,elements[i]);
        }
        return pairs;
    }
    private static void dfs(String s, ArrayList<Map<String,Object>> parent, AES256Util aes) throws WebException {
        int pos = s.indexOf("/");
        if(pos == -1) {
            for(Map<String,Object> child : parent) {
            	logger.info("s : " + s);
            	logger.info("child.get(s) : " + child.get(s));
            	if(child.get(s) == null) continue;
				child.put(s,Base64.getEncoder().encodeToString(aes.encrypt(((String) child.get(s)).getBytes())));
            }
        } else {
            String token = s.substring(0,pos);
            String ss = s.substring(pos + 1);
            for(Map<String,Object> child : parent) {
                Object childElement = child.get(token);
                logger.debug(token);
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
    
    public static void encrypt(String keystorePath,String passWord,String[] encryptElements,
                                              Map<String,Object> reqParam,Map<String,Object> response) throws WebException {
        SecureRandom rand = new SecureRandom();
        String sPubKey = new String(loadCertFromKeyStore(keystorePath,passWord).getPublicKey().getEncoded());
        
        String cPubKey = new String(Base64.getDecoder().decode((String) reqParam.get(WASJSONConsts.STRING_PUBKEY)));
        if(!sPubKey.equals(cPubKey)) {
        	logger.error("sPubkey : " + sPubKey);
        	logger.error("cPubkey : " + cPubKey);
            WebException e = new WebException("PublicKey Incorrect",WebException.RENCM_ENCRYPT_PUBKEY_INCORRECT);
            throw e;
        }
        byte[] cKey = Base64.getDecoder().decode((String) reqParam.get(WASJSONConsts.STRING_CKEY));
        byte[] privKey = getPrivateKeyFromKeyStore(keystorePath,passWord);
        cKey = RSAModule.decryptRSA(privKey,cKey);
        logger.info("cKeyBase64 : " + Base64.getEncoder().encodeToString(cKey));
        byte[] sKey = new byte[16];
        rand.nextBytes(sKey);
        logger.info("sKeyBase64 : " + Base64.getEncoder().encodeToString(sKey));
        byte[] key = new byte[32];
        System.arraycopy(cKey,0,key,0,16);
        System.arraycopy(sKey,0,key,16,16);
        AES256Util aes = new AES256Util(key);
        logger.info("keyBase64 : " + Base64.getEncoder().encodeToString(key));
        for(String element : encryptElements) {
            ArrayList<Map<String,Object>> maps = new ArrayList<>();
            maps.add(response);
            dfs(element,maps,aes);
        }
        String ret = Base64.getEncoder().encodeToString(RSAModule.encryptRSA(privKey,sKey));
        Map<String,Object>[] encryptedElements = ResponseEncryptModule.setEncryptElements(encryptElements);

        response.put(WASJSONConsts.JO_ENCRYPTED_ELEMENTS,encryptedElements);
        response.put(WASJSONConsts.STRING_SKEY,ret);
    }

}
