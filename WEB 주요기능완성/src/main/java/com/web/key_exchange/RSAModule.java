package com.web.key_exchange;


import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.web.exception.WebException;

import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class RSAModule {
	private static final Logger logger = LoggerFactory.getLogger(RSAModule.class);
	
    public static byte[] encryptRSA(byte[] key,byte[] input) throws WebException {
        Cipher cipher;
		try {
			cipher = Cipher.getInstance("RSA/ECB/PKCS1PADDING","SunJCE");
		

			// Turn the encoded key into a real RSA public key.
	        // Public keys are encoded in X.509.
	        X509EncodedKeySpec ukeySpec = new X509EncodedKeySpec(key);
	        KeyFactory ukeyFactory = KeyFactory.getInstance("RSA");
	        PublicKey publicKey = null;
	        PrivateKey privateKey = null;
	        try {
	            publicKey = ukeyFactory.generatePublic(ukeySpec);
	        } catch (InvalidKeySpecException e) {
	            PKCS8EncodedKeySpec rKeySpec = new PKCS8EncodedKeySpec(key);
	            KeyFactory rkeyFactory = KeyFactory.getInstance("RSA");
				privateKey = rkeyFactory.generatePrivate(rKeySpec);
	            cipher.init(Cipher.ENCRYPT_MODE, privateKey);
	            logger.info("EncryptWithPrivateKey");
	            byte[] cipherText = cipher.doFinal(input);
	            return cipherText;
	        }
	
	        // 공개키를 전달하여 암호화
	        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
	        logger.info("EncryptWithPublicKey");
	        byte[] cipherText = cipher.doFinal(input);
	        return cipherText;
		}  catch (NoSuchAlgorithmException | NoSuchProviderException | NoSuchPaddingException e) {
	    	throw new WebException("Can not get Instance [RSA]", WebException.RSA_ENC_NO_INSTANCE,e);
	    } catch (InvalidKeyException | InvalidKeySpecException | IllegalBlockSizeException | BadPaddingException e) {
	    	throw new WebException("Invalid keybytes for RSA", WebException.RSA_ENC_INV_KEY, e);
		}
	}
    public static byte[] decryptRSA(byte[] key, byte[] cipherText) throws WebException {
        Cipher cipher;
		try {
			cipher = Cipher.getInstance("RSA/ECB/PKCS1PADDING","SunJCE");
	        // Turn the encoded key into a real RSA private key.
	        // Private keys are encoded in PKCS#8.
	        PKCS8EncodedKeySpec rkeySpec = new PKCS8EncodedKeySpec(key);
	        KeyFactory rkeyFactory = KeyFactory.getInstance("RSA");
	        PublicKey publicKey = null;
	        PrivateKey privateKey = null;
	        try {
	            privateKey = rkeyFactory.generatePrivate(rkeySpec);
	        } catch (InvalidKeySpecException e) {
	            X509EncodedKeySpec ukeySpec = new X509EncodedKeySpec(key);
	            KeyFactory ukeyFactory = KeyFactory.getInstance("RSA");
				publicKey = ukeyFactory.generatePublic(ukeySpec);
	            cipher.init(Cipher.DECRYPT_MODE, publicKey);
	            logger.info("DecryptWithPublicKey");
	            byte[] plainText = cipher.doFinal(cipherText);
	            return plainText;
	        }
	        cipher.init(Cipher.DECRYPT_MODE, privateKey);
	        logger.info("DecryptWithPrivateKey");
	        byte[] plainText = cipher.doFinal(cipherText);
	        return plainText;
        
	    } catch (NoSuchAlgorithmException | NoSuchProviderException | NoSuchPaddingException e) {
	    	throw new WebException("Can not get Instance [RSA]",WebException.RSA_DEC_NO_INSTANCE ,e);
	    } catch (InvalidKeyException | InvalidKeySpecException | IllegalBlockSizeException | BadPaddingException e) {
	    	throw new WebException("Invalid keybytes for RSA", WebException.RSA_DEC_INV_KEY,e);
		}
    }

}

