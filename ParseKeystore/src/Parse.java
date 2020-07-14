import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class Parse {
    private static String byteArrayToHex(byte[] ba) {
        if (ba == null || ba.length == 0) {
            return null;
        }
        StringBuffer sb = new StringBuffer(ba.length * 2);
        String hexNumber;
        for (int x = 0; x < ba.length; x++) {
            hexNumber = "0" + Integer.toHexString(0xff & ba[x]);

            sb.append(hexNumber.substring(hexNumber.length() - 2));
        }
        return sb.toString();
    }
    private static byte[] encryptRSA(byte[] pubKey,byte[] input)
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
            System.out.println("pubKeyHex:"+byteArrayToHex(publicKey.getEncoded()));
        } catch (InvalidKeySpecException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // 공개키를 전달하여 암호화
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] cipherText = cipher.doFinal(input);
        System.out.println("Encrypted Hex:"+byteArrayToHex(cipherText));
        return cipherText;
    }
    private static byte[] decryptRSA(byte[] privKey, byte[] cipherText) throws NoSuchAlgorithmException, NoSuchProviderException,
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
        System.out.println("decryptedAESKEY Hex: " + byteArrayToHex(plainText));
        return plainText;
    }

    public static void main(String args[]) throws KeyStoreException, CertificateException,
            NoSuchAlgorithmException, IOException, UnrecoverableKeyException,
            IllegalBlockSizeException, InvalidKeyException, BadPaddingException,
            NoSuchProviderException, NoSuchPaddingException, InvalidKeySpecException {
        KeyStore keystore = KeyStore.getInstance("PKCS12");
        InputStream is = new FileInputStream(new File("keystore.p12"));
        keystore.load(is, "123456".toCharArray());
        String alias = keystore.aliases().nextElement();
        PrivateKey key = (PrivateKey)keystore.getKey(alias, "123456".toCharArray());
        System.out.println(alias);
        X509Certificate certificate = (X509Certificate) keystore.
                getCertificate(alias);

        System.out.println(new String(certificate.getEncoded()));
        PublicKey publicKey = certificate.getPublicKey();

        System.out.println(byteArrayToHex(key.getEncoded()));
        System.out.println(byteArrayToHex(publicKey.getEncoded()));
        byte[] cipherText = encryptRSA(publicKey.getEncoded(),"hello".getBytes());
        byte[] result = decryptRSA(key.getEncoded(),cipherText);
        System.out.println(new String(result));

        Base64.Encoder encoder = Base64.getEncoder();
        String test = new String(encoder.encode(publicKey.getEncoded()));
        System.out.println(test);
        System.out.println(byteArrayToHex(encoder.encode(publicKey.getEncoded())));
        System.out.println(byteArrayToHex(test.getBytes()));
        FileOutputStream out = new FileOutputStream("pub.cer");
        out.write(certificate.getEncoded());

    }
}
