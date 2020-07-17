import java.io.*;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.*;

public class Parse {
    public static void main(String args[]) throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException, UnrecoverableKeyException {
        //InputStream is = new ByteArrayInputStream(cert.getBytes());
        InputStream is = new FileInputStream(new File("han1g.pfx"));
        KeyStore keystore = KeyStore.getInstance("PKCS12");
        keystore.load(is, "1q2w3e4r5t@".toCharArray());
        String alias = keystore.aliases().nextElement();
        PrivateKey key = (PrivateKey)keystore.getKey(alias, "1q2w3e4r5t@".toCharArray());
        //System.out.println(alias);
        X509Certificate certificate = (X509Certificate) keystore.
                getCertificate(alias);
        System.out.println(Base64.getEncoder().encodeToString(certificate.getEncoded()));
        System.out.println(Base64.getEncoder().encodeToString(key.getEncoded()));

        /*InputStream is = new FileInputStream("intermed-ca.der");
        CertificateFactory factory = CertificateFactory.getInstance("X.509");
        X509Certificate certificate = (X509Certificate) factory.generateCertificate(is);*/
        String issuer = certificate.getIssuerDN().getName();
        String subject = certificate.getSubjectDN().getName();
        String notBefore = certificate.getNotBefore().toString();
        String notAfter = certificate.getNotAfter().toString();
        System.out.println(certificate.getVersion());
        System.out.println(certificate.getSerialNumber());
        System.out.println(certificate.getSigAlgName());
        //System.out.println(sighashAlgorithm);
        System.out.println("issuer : " + issuer + " " +issuer.length());
        System.out.println("notBefore : " + notBefore);
        System.out.println("notAfter : " + notAfter);
        System.out.println("subject : " + subject);
        System.out.println(certificate.getPublicKey().getAlgorithm());
        System.out.println(byteToString(certificate.getPublicKey().getEncoded()));
        System.out.println(booleanToString(certificate.getIssuerUniqueID()));
        System.out.println(booleanToString(certificate.getSubjectUniqueID()));
        System.out.println(byteToString(certificate.getSignature()));

        try {
            System.out.println(parseName(certificate.getIssuerDN().getName(),"CN"));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    private static String byteToString(byte[] bytes) {
        System.out.println("length: " + bytes.length);
        String ret = "";
        for(int i = 0;i < bytes.length;i++) {
            if(i < bytes.length - 1) {
                if(bytes[i] < 0x10 && bytes[i] >= 0)
                    ret += String.format("0%x:", bytes[i]);
                else
                    ret += String.format("%x:", bytes[i]);
            }
            else {
                if(bytes[i] < 0x10 && bytes[i] >= 0)
                    ret += String.format("0%x", bytes[i]);
                else
                    ret += String.format("%x", bytes[i]);
            }
        }
        return ret;
    }
    private static String booleanToString(boolean[] bs) {
        if(bs == null)
            return "null";
        String ret = "";
        for(int i = 0 ; i < bs.length;i++) {
            if(bs[i]) {
                ret += "1";
            }else {
                ret += "0";
            }
            if(i < bs.length - 1)
                ret+=":";
        }
        return ret;
    }
   private static String parseName(String s,String ss) throws Exception {
        ss = ss.toUpperCase();
        int startPos = s.indexOf(ss + "=");
        if(startPos != -1) {
            startPos += ss.length() + 1;
            s = s.substring(startPos);
            if(s.charAt(0) == '"') {
                s = s.substring(1);
                int endPos = s.indexOf('"');
                return s.substring(0,endPos);
            }
            else {
                int endPos = s.indexOf(',');
                if (endPos == -1) {
                    return s;
                }
                return s.substring(0,endPos);
            }
        } else {
            throw new Exception("Name doesn't have " + ss);
        }
    }

}
