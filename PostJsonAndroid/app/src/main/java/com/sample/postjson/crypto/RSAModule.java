package com.sample.postjson.crypto;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

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
}
