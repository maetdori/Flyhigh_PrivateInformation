import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.*;

public class Parse {
    static String cert = "-----BEGIN CERTIFICATE-----\n" +
            "MIIDRTCCAi2gAwIBAgIEO+IxfTANBgkqhkiG9w0BAQsFADBTMQswCQYDVQQGEwJr\n" +
            "cjEMMAoGA1UECBMDZWVlMQwwCgYDVQQHEwNkZGQxDDAKBgNVBAoTA2NjYzEMMAoG\n" +
            "A1UECxMDYmJiMQwwCgYDVQQDEwNhYWEwHhcNMjAwNzA1MDcxMjE1WhcNMzEwNjE4\n" +
            "MDcxMjE1WjBTMQswCQYDVQQGEwJrcjEMMAoGA1UECBMDZWVlMQwwCgYDVQQHEwNk\n" +
            "ZGQxDDAKBgNVBAoTA2NjYzEMMAoGA1UECxMDYmJiMQwwCgYDVQQDEwNhYWEwggEi\n" +
            "MA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQCnN7QtLvw4trqSx44yw6w41zhC\n" +
            "Hz2AMKquXCoz9fOO95sUOA+aQl5g7D/5GFq1c0qpVT+UurHfNgtKh4S2ZHblU0Xj\n" +
            "llOZu36daMe2RadbZkvRREtM3VXoFolHpRNBKLOhC4hV48ZY4wEAWO7NygdgK2ez\n" +
            "DWxFFd/yNwQfXjE1dLnjbUfxnKZyMFUfMofK1EhR3LRw1T/xZdpeigIEpPiLtfhE\n" +
            "6IJemtpkpSzdmyq8TQMTj1zrfAe8Km7GSxQ0HEpwXd4PQ7+NfNq45JZzrji12eTu\n" +
            "LAszie+hLzVNWgLot+7GRpbNAgyWDj7lW0MBxmS5yMKW4SoKnp8t/tgTTsPZAgMB\n" +
            "AAGjITAfMB0GA1UdDgQWBBQThHxZ48Gh1VWhctZKsajGnNdzBDANBgkqhkiG9w0B\n" +
            "AQsFAAOCAQEASMQE1K7rD5xkg9FZpUmS8ihVJb5SIJAgxHCx5pE5SBEfr+OryHFd\n" +
            "cEk6DOEstFX8zsRbqdSqVyrIU43zOM856S+V46f80k0ZPyRKgdva2nmGPvTqwPiN\n" +
            "1JW70HkcPVDhsDC/cPMTuerfiYJq2l56vGOiDdJ8nJ+2nmBmTZBAgto2npy5hHtV\n" +
            "58JS6ZckN+7jYGcVkpq+BUqGQ7cDFBBdQQSF4TIQDFEMQeM+op/wPBAiKENR0BjA\n" +
            "xIYBIkU8I7ch/JXfbEDiLdQ/hBjIbD26o+SiDmk/zuT3jccEbXSw0O0kOyuj0liD\n" +
            "J6xn1IJz/xIQPIEX0omnJQMEjrmxx4+5Rg==\n" +
            "-----END CERTIFICATE-----\n";

    public static void main(String args[]) throws CertificateException {
        InputStream is = new ByteArrayInputStream(cert.getBytes());
        CertificateFactory factory = CertificateFactory.getInstance("X.509");
        X509Certificate certificate = (X509Certificate) factory.generateCertificate(is);
        String issuer = certificate.getIssuerDN().getName();
        String subject = certificate.getSubjectDN().getName();
        String notBefore = certificate.getNotBefore().toString();
        String notAfter = certificate.getNotAfter().toString();
        System.out.println("issuer : " + issuer);
        System.out.println("subject : " + subject);
        System.out.println("notBefore : " + notBefore);
        System.out.println("notAfter : " + notAfter);


        String url = "https://localhost:8080/api";
        int pos = url.indexOf("/");
        String tmp = url.substring(pos + 2);
        pos += tmp.indexOf("/");
        pos += 2;
        String host = url.substring(0,pos);
        System.out.println(host);
        strings(new String[] {"1","2"});

        Map<String,Object> s  = new HashMap<>();
        s.put("a","b");
        ArrayList<Map<String,Object>> list = new ArrayList<>();
        list.add(s);
        s.put("a","c");
        System.out.println(list.get(0));
        System.out.println(list instanceof List);

        byte[] a = {1,2,3,4};
        String aa = new String(a);
        byte[] b = {1,2,3,5};
        String bb = new String(b);
        System.out.println(aa.equals(bb));
    }
    private static void strings(String[] s) {
        System.out.println(s[0]);
        System.out.println(s[1]);
    }

}
