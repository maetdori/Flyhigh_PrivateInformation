package com.was.key_exchange;

import java.security.GeneralSecurityException;
import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AES128Util {
    private byte[] iv;
    private Key keySpec;
    public AES128Util(byte[] key) {
        iv = new byte[16];
        System.arraycopy(key,0,iv,0,16);

        byte[] keyBytes = new byte[16];
        System.arraycopy(key, 16, keyBytes, 0, 16);
        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
        this.keySpec = keySpec;
    }

    public byte[] encrypt(String str) throws GeneralSecurityException {
        Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
        byte[] iv = this.iv;
        c.init(Cipher.ENCRYPT_MODE, keySpec, new IvParameterSpec(iv));
        byte[] encrypted = c.doFinal(str.getBytes());
        return encrypted;
    }
    public String decrypt(byte[] str) throws
            GeneralSecurityException {
        Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
        c.init(Cipher.DECRYPT_MODE, keySpec, new IvParameterSpec(iv));
        byte[] decrypted = c.doFinal(str);
        return new String(decrypted);
    }
}