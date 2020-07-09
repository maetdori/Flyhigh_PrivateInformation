package com.prj.key_exhange;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class RSAModule {
    public static byte[] encryptRSA(byte[] pubKey,byte[] input)
            throws NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1PADDING", "SunJCE");

        // Turn the encoded key into a real RSA public key.
        // Public keys are encoded in X.509.
        X509EncodedKeySpec ukeySpec = new X509EncodedKeySpec(pubKey);
        KeyFactory ukeyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = null;
        try {
            publicKey = ukeyFactory.generatePublic(ukeySpec);
        } catch (InvalidKeySpecException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // 공개키를 전달하여 암호화
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] cipherText = cipher.doFinal(input);
        return cipherText;
    }
    public static byte[] decryptRSA(byte[] privKey, byte[] cipherText) throws NoSuchAlgorithmException, NoSuchProviderException,
            NoSuchPaddingException, InvalidKeySpecException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1PADDING", "SunJCE");
        // Turn the encoded key into a real RSA private key.
        // Private keys are encoded in PKCS#8.
        PKCS8EncodedKeySpec rkeySpec = new PKCS8EncodedKeySpec(privKey);
        KeyFactory rkeyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = null;
        privateKey = rkeyFactory.generatePrivate(rkeySpec);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] plainText = cipher.doFinal(cipherText);
        return plainText;
    }

}
