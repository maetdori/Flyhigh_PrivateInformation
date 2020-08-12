package com.private_information;

import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.private_information.APIException;

public class AES256Util {
    private Key keySpec;
    public AES256Util(byte[] key) {

        SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
        this.keySpec = keySpec;
    }

    public byte[] encrypt(byte[] str) throws APIException {
        try {
            Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
            byte[] iv = new byte[16];
            SecureRandom random = new SecureRandom();
            random.nextBytes(iv);
            c.init(Cipher.ENCRYPT_MODE, keySpec, new IvParameterSpec(iv));
            byte[] encrypted = c.doFinal(str);
            byte[] ret = new byte[16 + encrypted.length];
            System.arraycopy(iv,0,ret,0,16);
            System.arraycopy(encrypted,0,ret,16,encrypted.length);
            return ret;
        } catch(GeneralSecurityException e) {
            throw new APIException("Failed Encrypt data (" + new String(str)+ ")" , APIException.AES256UTIL,e);
        }
    }
    public byte[] decrypt(byte[] str) throws APIException {
        try {
            byte[] iv = new byte[16];
            byte[] enc = new byte[str.length - 16];
            System.arraycopy(str,0,iv,0,16);
            System.arraycopy(str,16,enc,0,enc.length);
            Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
            c.init(Cipher.DECRYPT_MODE, keySpec, new IvParameterSpec(iv));
            byte[] decrypted = c.doFinal(enc);
            return decrypted;
        } catch(GeneralSecurityException e) {
            throw new APIException("Failed Decrypt data (" + new String(str) + ")" , APIException.AES256UTIL,e);
        }
    }
}