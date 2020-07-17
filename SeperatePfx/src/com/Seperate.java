package com;


import org.bouncycastle.asn1.*;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.PBEParametersGenerator;
import org.bouncycastle.crypto.generators.PKCS5S2ParametersGenerator;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.ByteBuffer;
import java.security.*;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Enumeration;

public class Seperate {
    public static void main(String args[]) throws Exception {
        Security.addProvider(new BouncyCastleProvider());
        //get pfx
        KeyStore keystore = KeyStore.getInstance("PKCS12","SunJSSE");
        InputStream is = new FileInputStream(new File("han1g.pfx"));
        keystore.load(is, "1q2w3e4r5t@".toCharArray());


        Enumeration<String> aliases = keystore.aliases();
        String alias = aliases.nextElement();
        System.out.println("alias:" + alias );
        KeyStore.Entry e = keystore.getEntry(alias,new KeyStore.PasswordProtection("1q2w3e4r5t@".toCharArray()));
        System.out.println(e.toString());
        for(KeyStore.Entry.Attribute attr : e.getAttributes()) {
            System.out.println(attr.toString());
        }
        //get der
        X509Certificate certificate = (X509Certificate) keystore.
                getCertificate(alias);
        System.out.println(Base64.getEncoder().encodeToString(certificate.getEncoded()));

        //get key

        PrivateKey deckey = (PrivateKey) keystore.getKey(alias, "1q2w3e4r5t@".toCharArray());
        System.out.println("deckey format: " + deckey.getFormat());
        System.out.println(Base64.getEncoder().encodeToString(deckey.getEncoded()));

        PBEParametersGenerator generator = new PKCS5S2ParametersGenerator();
        byte[] salt = new byte[] {(byte) 0xAD,
                (byte) 0x55,
                (byte) 0x12,
                (byte) 0x34,
                (byte)0x35,
                (byte) 0x62,
                (byte) 0x11,
                (byte) 0x24};
        int ic = 2048;
        byte[] dk = new byte[20];
        MessageDigest md = MessageDigest.getInstance("SHA1");
        md.update("1q2w3e4r5t@".getBytes());
        md.update(salt);
        dk = md.digest();
        for (int i = 1; i < ic; i++) {
            dk = md.digest(dk);
        }
        byte[] keyData = new byte[16];
        System.arraycopy(dk, 0, keyData, 0, 16);
        byte[] digestBytes = new byte[4];
        System.arraycopy(dk, 16, digestBytes, 0, 4);
        MessageDigest digest = MessageDigest.getInstance("SHA-1");
        digest.reset();
        digest.update(digestBytes);
        byte[] div = digest.digest();
        // --------------------------------
        // Initial Vector(IV) 생성
        // --------------------------------
        byte[] iv = new byte[16];
        System.arraycopy(div, 0, iv, 0, 16);
        System.out.println("IV: " +byteArrToHex(iv));

        //saltOffset: 0x24
        //IVOffset: 0x3E
        //datalengthOffset:0x50
        //dataoffset: 0x52
        is = new FileInputStream("signPri.key");
        byte[] data = is.readNBytes(0x50);
        ByteBuffer buffer = ByteBuffer.wrap(data);
        buffer.position(0x24);
        buffer.put(salt);
        buffer.position(0x3E);
        buffer.put(iv);

        byte[] random = new byte[20];
        new SecureRandom().nextBytes(random);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ByteBuffer buffer2 = ByteBuffer.wrap(deckey.getEncoded());
        buffer2.position(2);
        buffer2.put(lengthToByteArray(deckey.getEncoded().length -4 + 41));
        outputStream.write(buffer2.array());
        outputStream.write( new byte[] {(byte) 0xA0, 0x27 , 0x30 , 0x25 , 0x06 , 0x0A  , 0x2A , (byte) 0x83, 0x1A , (byte) 0x8C, (byte) 0x9A, 0x44 , 0x0A , 0x01
                , 0x01 , 0x03 , 0x31 , 0x17 , 0x03 , 0x15 , 0x00} );
        outputStream.write(random);
        byte[] deckeyAndRandom = outputStream.toByteArray();
        System.out.println("DecKey and Random : " + Base64.getEncoder().encodeToString(deckeyAndRandom));

        generator.init( PBEParametersGenerator.PKCS5PasswordToBytes("1q2w3e4r5t@".toCharArray()), salt, ic);
        //encryptkey with password
        int keySize = 256;
        KeyParameter extractKey = (KeyParameter)generator.generateDerivedParameters(keySize);
        byte[] extractKeyBytes = extractKey.getKey();
        byte[] secKeyBytes = new byte[16];
        System.arraycopy(extractKeyBytes,0,secKeyBytes,0,16);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        SecretKeySpec secKey = new SecretKeySpec(secKeyBytes,"SEED");
        Cipher cipher = Cipher.getInstance("SEED/CBC/PKCS5Padding","BC");
        cipher.init(Cipher.ENCRYPT_MODE,secKey,ivSpec);
        byte[] encKey = cipher.doFinal(deckeyAndRandom);
        byte[] output = new byte[encKey.length + 2];
        System.arraycopy(lengthToByteArray(encKey.length),0,output,0,2);
        System.arraycopy(encKey,0,output,2,encKey.length);
        System.out.println("Self Encrypted Data: " + Base64.getEncoder().encodeToString(encKey));

        PrivateKey keyFileKey = readPrivateKey("signPri.key","1q2w3e4r5t@");
        System.out.println(Base64.getEncoder().encodeToString(keyFileKey.getEncoded()));

        buffer.position(2);
        buffer.put(lengthToByteArray(74 + encKey.length + 4));
        FileOutputStream os = new FileOutputStream("signPri-edit.key");
        os.write(buffer.array());
        os.write(output);
    }
    private static byte[] getEncryptedData(String filePath, String passwd) throws IOException, NoSuchPaddingException,
            NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException {
        byte[] decryptedKey = null;
        FileInputStream fis = new FileInputStream(filePath);
        byte[] encodedKey = fis.readAllBytes();
        org.bouncycastle.asn1.pkcs.EncryptedPrivateKeyInfo encryptedPrivateKeyInfo = null;
        try (ByteArrayInputStream bIn = new ByteArrayInputStream(encodedKey);
             ASN1InputStream aIn = new ASN1InputStream(bIn);) {
            ASN1Sequence asn1Sequence = (ASN1Sequence) aIn.readObject();
            AlgorithmIdentifier algId = AlgorithmIdentifier.getInstance(asn1Sequence.getObjectAt(0));
            ASN1OctetString data = ASN1OctetString.getInstance(asn1Sequence.getObjectAt(1));
            System.out.println("EncryptedData :" + Base64.getEncoder().encodeToString(data.getOctets()));
            encryptedPrivateKeyInfo = new org.bouncycastle.asn1.pkcs.EncryptedPrivateKeyInfo(algId, data.getEncoded());
            String privateKeyAlgName = encryptedPrivateKeyInfo.getEncryptionAlgorithm().getAlgorithm().getId();
            if ("1.2.840.113549.1.5.13".equals(privateKeyAlgName)) {
                // pkcs5PBES2
                // --------------------------------
                // 개인키 암호화 정보에서 Salt, Iteration Count(IC), Initial Vector(IV)를 가져오는 로직
                // --------------------------------
                ASN1Sequence asn1Sequence2 = (ASN1Sequence) algId.getParameters();
                ASN1Sequence asn1Sequence3 = (ASN1Sequence) asn1Sequence2.getObjectAt(0);
                //PBKDF2 Key derivation algorithm
                ASN1Sequence asn1Sequence33 = (ASN1Sequence) asn1Sequence3.getObjectAt(1);
                // Salt 값
                DEROctetString derOctetStringSalt = (DEROctetString) asn1Sequence33.getObjectAt(0);
                System.out.println("Salt :" + byteArrToHex(derOctetStringSalt.getOctets()));
                // Iteration Count(IC)
                ASN1Integer asn1IntegerIC = (ASN1Integer) asn1Sequence33.getObjectAt(1);
                System.out.println("IC :" + asn1IntegerIC.getValue().intValue());
                ASN1Sequence asn1Sequence4 = (ASN1Sequence) asn1Sequence2.getObjectAt(1);
                // Initial Vector(IV)
                DEROctetString derOctetStringIV = (DEROctetString) asn1Sequence4.getObjectAt(1);
                // --------------------------------
                // 복호화 키 생성
                // --------------------------------
                int keySize = 256;
                PBEParametersGenerator generator = new PKCS5S2ParametersGenerator();
                generator.init(PBEParametersGenerator.PKCS5PasswordToBytes(passwd.toCharArray()),
                        derOctetStringSalt.getOctets(), asn1IntegerIC.getValue().intValue());
                byte[] iv = derOctetStringIV.getOctets();
                KeyParameter key =
                        (KeyParameter) generator.generateDerivedParameters(keySize);
                // --------------------------------
                // 복호화 수행
                // --------------------------------
                IvParameterSpec ivSpec = new IvParameterSpec(iv);
                SecretKeySpec secKey = new SecretKeySpec(key.getKey(), "SEED");
                Cipher cipher = Cipher.getInstance("SEED/CBC/PKCS5Padding", "BC");
                cipher.init(Cipher.DECRYPT_MODE, secKey, ivSpec);
                decryptedKey = cipher.doFinal(data.getOctets());
            }
            else { // 1.2.410.200004.1.15 seedCBCWithSHA1
                ASN1Sequence asn1Sequence2 = (ASN1Sequence) algId.getParameters();
                // Salt 값
                DEROctetString derOctetStringSalt = (DEROctetString) asn1Sequence2.getObjectAt(0);
                // Iteration Count(IC)
                ASN1Integer asn1IntegerIC = (ASN1Integer) asn1Sequence2.getObjectAt(1);
                // --------------------------------
                // 복호화 키 생성
                // --------------------------------
                byte[] dk = new byte[20];
                MessageDigest md = MessageDigest.getInstance("SHA1");
                md.update(passwd.getBytes());
                md.update(derOctetStringSalt.getOctets());
                dk = md.digest();
                for (int i = 1; i < asn1IntegerIC.getValue().intValue(); i++) {
                    dk = md.digest(dk);
                }
                byte[] keyData = new byte[16];
                System.arraycopy(dk, 0, keyData, 0, 16);
                byte[] digestBytes = new byte[4];
                System.arraycopy(dk, 16, digestBytes, 0, 4);
                MessageDigest digest = MessageDigest.getInstance("SHA-1");
                digest.reset();
                digest.update(digestBytes);
                byte[] div = digest.digest();
                // --------------------------------
                // Initial Vector(IV) 생성
                // --------------------------------
                byte[] iv = new byte[16];
                System.arraycopy(div, 0, iv, 0, 16);
                if ("1.2.410.200004.1.4".equals(privateKeyAlgName)) {
                    iv = "012345678912345".getBytes();
                }
                // --------------------------------
                // 복호화 수행
                // --------------------------------
                IvParameterSpec ivSpec = new IvParameterSpec(iv);
                SecretKeySpec secKey = new SecretKeySpec(keyData, "SEED");
                Cipher cipher = Cipher.getInstance("SEED/CBC/PKCS5Padding", "BC");
                cipher.init(Cipher.DECRYPT_MODE, secKey, ivSpec);
                decryptedKey = cipher.doFinal(data.getOctets());

            }
        }
        return decryptedKey;
    }

    public static PrivateKey readPrivateKey(String filePath, String passwd) throws Exception {
        byte[] decryptedKey = null;
        FileInputStream fis = new FileInputStream(filePath);
        byte[] encodedKey = fis.readAllBytes();
        org.bouncycastle.asn1.pkcs.EncryptedPrivateKeyInfo encryptedPrivateKeyInfo = null;
        try (ByteArrayInputStream bIn = new ByteArrayInputStream(encodedKey);
             ASN1InputStream aIn = new ASN1InputStream(bIn);) {
            ASN1Sequence asn1Sequence = (ASN1Sequence) aIn.readObject();
            AlgorithmIdentifier algId = AlgorithmIdentifier.getInstance(asn1Sequence.getObjectAt(0));
            ASN1OctetString data = ASN1OctetString.getInstance(asn1Sequence.getObjectAt(1));
            System.out.println("EncryptedData :" + Base64.getEncoder().encodeToString(data.getOctets()));
            encryptedPrivateKeyInfo = new org.bouncycastle.asn1.pkcs.EncryptedPrivateKeyInfo(algId, data.getEncoded());
            String privateKeyAlgName = encryptedPrivateKeyInfo.getEncryptionAlgorithm().getAlgorithm().getId();
            if ("1.2.840.113549.1.5.13".equals(privateKeyAlgName)) {
                // pkcs5PBES2
                // --------------------------------
                // 개인키 암호화 정보에서 Salt, Iteration Count(IC), Initial Vector(IV)를 가져오는 로직
                // --------------------------------
                ASN1Sequence asn1Sequence2 = (ASN1Sequence) algId.getParameters();
                ASN1Sequence asn1Sequence3 = (ASN1Sequence) asn1Sequence2.getObjectAt(0);
                //PBKDF2 Key derivation algorithm
                ASN1Sequence asn1Sequence33 = (ASN1Sequence) asn1Sequence3.getObjectAt(1);
                // Salt 값
                DEROctetString derOctetStringSalt = (DEROctetString) asn1Sequence33.getObjectAt(0);
                System.out.println("Salt :" + byteArrToHex(derOctetStringSalt.getOctets()));
                // Iteration Count(IC)
                ASN1Integer asn1IntegerIC = (ASN1Integer) asn1Sequence33.getObjectAt(1);
                System.out.println("IC :" + asn1IntegerIC.getValue().intValue());
                ASN1Sequence asn1Sequence4 = (ASN1Sequence) asn1Sequence2.getObjectAt(1);
                // Initial Vector(IV)
                DEROctetString derOctetStringIV = (DEROctetString) asn1Sequence4.getObjectAt(1);
                // --------------------------------
                // 복호화 키 생성
                // --------------------------------
                int keySize = 256;
                PBEParametersGenerator generator = new PKCS5S2ParametersGenerator();
                generator.init(PBEParametersGenerator.PKCS5PasswordToBytes(passwd.toCharArray()),
                        derOctetStringSalt.getOctets(), asn1IntegerIC.getValue().intValue());
                byte[] iv = derOctetStringIV.getOctets();
                KeyParameter key =
                        (KeyParameter) generator.generateDerivedParameters(keySize);
                // --------------------------------
                // 복호화 수행
                // --------------------------------
                IvParameterSpec ivSpec = new IvParameterSpec(iv);
                SecretKeySpec secKey = new SecretKeySpec(key.getKey(), "SEED");
                Cipher cipher = Cipher.getInstance("SEED/CBC/PKCS5Padding", "BC");
                cipher.init(Cipher.DECRYPT_MODE, secKey, ivSpec);
                decryptedKey = cipher.doFinal(data.getOctets());
            }
            else { // 1.2.410.200004.1.15 seedCBCWithSHA1
                ASN1Sequence asn1Sequence2 = (ASN1Sequence) algId.getParameters();
                // Salt 값
                DEROctetString derOctetStringSalt = (DEROctetString) asn1Sequence2.getObjectAt(0);
                // Iteration Count(IC)
                ASN1Integer asn1IntegerIC = (ASN1Integer) asn1Sequence2.getObjectAt(1);
                // --------------------------------
                // 복호화 키 생성
                // --------------------------------
                byte[] dk = new byte[20];
                MessageDigest md = MessageDigest.getInstance("SHA1");
                md.update(passwd.getBytes());
                md.update(derOctetStringSalt.getOctets());
                dk = md.digest();
                for (int i = 1; i < asn1IntegerIC.getValue().intValue(); i++) {
                    dk = md.digest(dk);
                }
                byte[] keyData = new byte[16];
                System.arraycopy(dk, 0, keyData, 0, 16);
                byte[] digestBytes = new byte[4];
                System.arraycopy(dk, 16, digestBytes, 0, 4);
                MessageDigest digest = MessageDigest.getInstance("SHA-1");
                digest.reset();
                digest.update(digestBytes);
                byte[] div = digest.digest();
                // --------------------------------
                // Initial Vector(IV) 생성
                // --------------------------------
                byte[] iv = new byte[16];
                System.arraycopy(div, 0, iv, 0, 16);
                if ("1.2.410.200004.1.4".equals(privateKeyAlgName)) {
                    iv = "012345678912345".getBytes();
                }
                // --------------------------------
                // 복호화 수행
                // --------------------------------
                IvParameterSpec ivSpec = new IvParameterSpec(iv);
                SecretKeySpec secKey = new SecretKeySpec(keyData, "SEED");
                Cipher cipher = Cipher.getInstance("SEED/CBC/PKCS5Padding", "BC");
                cipher.init(Cipher.DECRYPT_MODE, secKey, ivSpec);
                decryptedKey = cipher.doFinal(data.getOctets());
            }
        }
        // --------------------------------
        // 복호화된 내용을 PrivateKey 객체로 변환
        // --------------------------------
        System.out.println("decrypted Data: " + byteArrToHex(decryptedKey));
        PKCS8EncodedKeySpec ks = new PKCS8EncodedKeySpec(decryptedKey);
        System.out.println("Decrypted DataBase64: " + Base64.getEncoder().encodeToString(decryptedKey));
        KeyFactory kf = KeyFactory.getInstance("RSA", "BC");
        return kf.generatePrivate(ks);
    }

    private static String byteArrToHex(byte[] bs) {
        String ret = "";
        for(int i = 0 ; i < bs.length;i++) {
            String tmp= "";
            if(bs[i] < 0x10 && bs[i] >= 0x00) {
                tmp = String.format("0%X",bs[i]);
                ret += tmp;
            }
            else {
                tmp = String.format("%X",bs[i]);
                ret += tmp;
            }
        }
        return ret;
    }
    public static byte[] lengthToByteArray(int value) {
        byte[] byteArray = new byte[2];
        byteArray[0] = (byte)(value >> 8);
        byteArray[1] = (byte)(value);
        return byteArray;
    }

}
