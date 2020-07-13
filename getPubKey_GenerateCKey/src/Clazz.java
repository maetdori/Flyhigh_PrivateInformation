import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

public class Clazz {
    public static void main(String[] args) throws CertificateException, NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, NoSuchProviderException, InvalidKeyException, InvalidKeySpecException, IOException {
        X509Certificate certificate = null;
        InputStream is = new FileInputStream("input.txt");
        byte[] bytes = is.readAllBytes();
        bytes = Base64.getDecoder().decode(bytes);
        is = new ByteArrayInputStream(bytes);
        certificate = (X509Certificate) CertificateFactory.getInstance("X509")
                .generateCertificate(is);


        PublicKey pubKey = certificate.getPublicKey();
        SecureRandom rand = new SecureRandom();
        byte[] cKey = new byte[16];
        rand.nextBytes(cKey);
        byte[] ecKey = RSAModule.encryptRSA(pubKey.getEncoded(), cKey);
        FileOutputStream os = new FileOutputStream("output.txt");
        System.out.println("\"pubKey\" : " + "\"" + Base64.getEncoder().encodeToString(pubKey.getEncoded()) + "\"");
        System.out.println("\"cKey\" : " + "\"" + Base64.getEncoder().encodeToString(ecKey) + "\"");
        String s = "\"pubKey\" : " + "\"" + Base64.getEncoder().encodeToString(pubKey.getEncoded()) + "\"";
        s += ",\n";
        s += "\"cKey\" : " + "\"" + Base64.getEncoder().encodeToString(ecKey) + "\"";
        os.write(s.getBytes());


    }
}
