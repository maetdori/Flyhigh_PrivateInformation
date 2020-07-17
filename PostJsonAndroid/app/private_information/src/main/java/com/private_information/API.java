package com.private_information;


import android.content.Context;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ProtocolException;
import java.net.URL;
import java.security.GeneralSecurityException;

import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import android.util.Base64;


import java.util.ArrayList;
import java.util.List;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import android.util.Log;


import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManagerFactory;
public class API {
    public static final int TYPE_WIFI = 1;
    public static final int TYPE_MOBILE = 2;
    public static final int TYPE_NOT_CONNECTED = 3;
    public static int getConnectivityStatus(Context context) { //해당 context의 서비스를 사용하기위해서 context객체를 받는다.
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if(networkInfo != null){
            int type = networkInfo.getType();
            if(type == ConnectivityManager.TYPE_MOBILE){//쓰리지나 LTE로 연결된것(모바일을 뜻한다.)
                return TYPE_MOBILE;
            }else if(type == ConnectivityManager.TYPE_WIFI){//와이파이 연결된것
                return TYPE_WIFI;
            }
        }
        return TYPE_NOT_CONNECTED;  //연결이 되지않은 상태
    }
    private static String convertInputStreamToString(InputStream inputStream) {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }
    private static SSLContext cert(String certPath) {
        // Load CAs from an InputStream
        // (could be from a resource or ByteArrayInputStream or ...)
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        InputStream caInput = new BufferedInputStream(new FileInputStream(certPath));
        Certificate ca;
        try {
            ca = cf.generateCertificate(caInput);
            Log.d("cert","ca=" + ((X509Certificate) ca).getSubjectDN());
        } finally {
            caInput.close();
        }

        // Create a KeyStore containing our trusted CAs
        String keyStoreType = KeyStore.getDefaultType();
        KeyStore keyStore = KeyStore.getInstance(keyStoreType);
        keyStore.load(null, null);
        keyStore.setCertificateEntry("ca", ca);

        // Create a TrustManager that trusts the CAs in our KeyStore
        String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
        tmf.init(keyStore);

        // Create an SSLContext that uses our TrustManager
        SSLContext context = SSLContext.getInstance("TLS");
        context.init(null, tmf.getTrustManagers(), null);

        return context;
    }
    private static HttpsURLConnection setConnection(Context context,String url) {
        SSLContext sslContext = null;
        try {
            sslContext = cert(context.getFilesDir().getAbsolutePath() + "/cert.cer");
            URL urlCon = new URL(url);
            HttpsURLConnection httpCon = (HttpsURLConnection) urlCon.openConnection();
            httpCon.setSSLSocketFactory(sslContext.getSocketFactory());
            httpCon.setHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;//default가 false 입니다.
                }
            });
            httpCon.connect();
            urlCon = new URL(url);
            httpCon = (HttpsURLConnection) urlCon.openConnection();
            httpCon.setSSLSocketFactory(sslContext.getSocketFactory());
            httpCon.setHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;//default가 false 입니다.
                }
            });
            return httpCon;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("set Connection","reconnect");
            URL urlCon = null;
            try {
                urlCon = new URL(url);
                HttpsURLConnection httpCon = (HttpsURLConnection) urlCon.openConnection();
                //httpCon.connect();
                return httpCon;
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }
    private static String requestCertBase64(Context context, String url) throws ProtocolException {
        HttpsURLConnection httpCon = setConnection(context,url);
        //getAndroidID
        TelephonyManager tm =(TelephonyManager)
                context.getSystemService(Context.TELEPHONY_SERVICE);
        String androidId = Settings.Secure.getString
                (context.getContentResolver(),Settings.Secure.ANDROID_ID);
        String transactionTime = null;

        //setHttpMethod
        httpCon.setRequestMethod("GET");
        //setHeaders
        httpCon.setRequestProperty("Content-type", "application/json");
        httpCon.setRequestProperty("Device-id", androidId);
        httpCon.setRequestProperty("TransactionTime",transactionTime);
        // OutputStream으로 POST 데이터를 넘겨주겠다는 옵션.
        httpCon.setDoOutput(false);

        // InputStream으로 서버로 부터 응답을 받겠다는 옵션.
        httpCon.setDoInput(true);
        String result = null;
        try {
            InputStream is = httpCon.getInputStream();
            // convert inputstream to string
            if(is != null)
                result = convertInputStreamToString(is);
            else
                result = "Did not work!";
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            httpCon.disconnect();
        }
        return result;
    }
    private static String getHost(String url) {
        int pos = url.indexOf("/");
        String tmp = url.substring(pos + 2);
        pos += tmp.indexOf("/");
        pos += 2;
        String host = url.substring(0,pos);
        return host;
    }
    private static X509Certificate getCert(Context context,String url) {
        X509Certificate certificate = null;

        String host = getHost(url);
        try {
            requestCertBase64(context,host + "/private/getCert");
        } catch (ProtocolException e) {
            e.printStackTrace();
            Log.e("MODULE.API.POSTSSL","PUBKEY CERTIFICATE IS NULL");
        }
        try {
            FileInputStream is = new FileInputStream(context.getFilesDir()
                    + File.separator + "/pub.cer");
            certificate = (X509Certificate) CertificateFactory.getInstance("X509")
                    .generateCertificate(is);
        } catch (IOException | CertificateException e) {
            e.printStackTrace();
            Log.e("MODULE.API.POSTSSL","ERROR LOADING PUBKEY CERTIFICATE");
        }
        if (certificate == null) {
            Log.e("MODULE.API.POSTSSL","PUBKEY CERTIFICATE IS NULL");
            return null;
        } else if (certificate.getNotAfter().getTime() < System.currentTimeMillis()) {
            Log.e("MODULE.API.POSTSSL","PUBKEY CERTIFICATE IS EXPIRED");
            return null;
        }

        return certificate;
    }
    private static String POSTSSL(Context context, String url,String... args)
            throws APIException {
        if(getConnectivityStatus(context) == TYPE_NOT_CONNECTED)
            throw new APIException("NO Internet",APIException.NO_INTERNET);

        X509Certificate certificate = getCert(context,url);
        //load publicKey Cert
        String result = null;
        HttpsURLConnection httpCon = setConnection(context,url);
        String json = "";
        JSONObject jsonObject = new JSONObject();

        PublicKey pubKey = certificate.getPublicKey();
        SecureRandom rand = new SecureRandom();
        byte[] cKey = new byte[16];
        rand.nextBytes(cKey);
        byte[] ecKey = RSAModule.encryptRSA(pubKey.getEncoded(), cKey);


        // build jsonObject
        int pos = url.lastIndexOf("/");
        String ext = url.substring(pos + 1);
        jsonObject.accumulate("pubKey",Base64.encodeToString(pubKey.getEncoded(),
                Base64.NO_WRAP));
        jsonObject.accumulate("cKey", Base64.encodeToString(ecKey, Base64.NO_WRAP));
        if (ext.equals("test")) {
            String s =" {\"a\":[{\"b\":[{\"c\":\"c1\"},{\"c\":\"c2\"},{\"c\":\"c3\"}]}," +
                    "  {\"b\":[{\"c\":\"c4\"},{\"c\":\"c5\"},{\"c\":\"c6\"}]}," +
                    "  {\"b\":[{\"c\":\"c7\"},{\"c\":\"c8\"},{\"c\":\"c9\"}]}]," +
                    " \"d\": \"d1\",\n" +
                    " \"e\" : \"e1\"}";
            jsonObject = new JSONObject(s);
            jsonObject.accumulate("pubKey",Base64.encodeToString(pubKey.getEncoded(),
                    Base64.NO_WRAP));
            jsonObject.accumulate("cKey", Base64.encodeToString(ecKey,
                    Base64.NO_WRAP));;
        }
        else if(ext.equals("getList")) {
            jsonObject.accumulate("username",args[0]);
        }
        else if(ext.equals("getInfo")) {
            jsonObject.accumulate("username",args[0]);
            jsonObject.accumulate("Subject",args[1]);
            JSONObject validity = new JSONObject();
            validity.accumulate("NotBefore",args[2]);
            jsonObject.accumulate("validity",validity);
            validity = new JSONObject();
            validity.accumulate("NotAfter",args[3]);
            jsonObject.accumulate("validity",validity);
        }
        // convert JSONObject to JSON to String
        json = jsonObject.toString();

        //getAndroidID
        /*TelephonyManager tm = (TelephonyManager)
                context.getSystemService(Context.TELEPHONY_SERVICE);*/
        String androidId = Settings.Secure.getString
                (context.getContentResolver(), Settings.Secure.ANDROID_ID);

        String transactionTime = "123456";
        //getServerCert
        httpCon.connect();
        X509Certificate cert =(X509Certificate) httpCon.getServerCertificates()[0];
        //setHttpMethod
        httpCon = setConnection(context,url);
        httpCon.setRequestMethod("POST");
        //setHeaders
        httpCon.setRequestProperty("Content-type", "application/json");
        httpCon.setRequestProperty("Device-id", androidId);
        httpCon.setRequestProperty("TransactionTime",transactionTime);
        httpCon.setRequestProperty("Server-Cert-Id",Base64.encodeToString(
                cert.getSignature(),Base64.NO_WRAP));

        // OutputStream으로 POST 데이터를 넘겨주겠다는 옵션.
        httpCon.setDoOutput(true);
        // InputStream으로 서버로 부터 응답을 받겠다는 옵션.
        httpCon.setDoInput(true);

        InputStream is = null;
        OutputStream os = httpCon.getOutputStream();
        os.write(json.getBytes("utf-8"));
        os.flush();
        // receive response as inputStream
        try {
            is = httpCon.getInputStream();
            // convert inputstream to string
            if (is != null)
                result = convertInputStreamToString(is);
            else
                result = "Did not work!";
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            httpCon.disconnect();
        }
        Log.i("RESPONSE JSON","result : " +result);
        if(result == null) return "null";
        JSONObject responseJSON = new JSONObject(result);
        JSONArray encryptedListJSON = null;
        try {
            encryptedListJSON = (JSONArray) responseJSON.get("encryptedElements");
        } catch(JSONException e) {
            return responseJSON.toString();
        }
        List<String> encList = new ArrayList<>();
        for(int i = 0; i < encryptedListJSON.length(); i++){
            encList.add(encryptedListJSON.getJSONObject(i).getString("name"));
        }
        Log.d("ckeyBase64" , Base64.encodeToString(cKey,Base64.NO_WRAP));
        byte[] key = new byte[32];
        System.arraycopy(cKey,0,key,0,16);
        byte[] sKey = Base64.decode(responseJSON.getString("sKey"),Base64.NO_WRAP);
        sKey = RSAModule.decryptRSA(pubKey.getEncoded(),sKey);
        Log.d("skeyBase64" , Base64.encodeToString(sKey,Base64.NO_WRAP));
        System.arraycopy(sKey,0,key,16,16);
        Log.d("keyBase64" , Base64.encodeToString(key,Base64.NO_WRAP));
        AES128Util aes = new AES128Util(key);

        for(String s :encList) {
            Log.d("ENCLIST",s);
            JsonDecryptModule.Decrypt(s,responseJSON,aes);
        }
        responseJSON.remove("encryptedElements");
        responseJSON.remove("sKey");
        result = responseJSON.toString(1);
        return result;
    }

    public static String getListPrivateInformation(Context context,String url,String username) {
        return POSTSSL(context,url + "/private/getList",username,null);
    }
    public static String getPrivateInformation(Context context, String url, String username,
                                               String subject, String notBefore, String notAfter) {
        String ret = null;
        try {
            ret = POSTSSL(context,url,username,subject,notBefore,notAfter);
        }catch(APIException e) {
            e.printStackTrace();
            Log.e("getPrivateInformation","Error while getting Data. get from DB");
            ret = DatabaseManager.getInstance(context).searchByNameFromInfo(subject);
            return ret;
        }
        try {
            DatabaseManager.getInstance(context).updatePrivateInformation(ret,subject);
        } catch (APIException e) {
            e.printStackTrace();
        }
        return ret;
    }
    public static String test(Context context, String url) {
        try {
            return POSTSSL(context,url + "/private/test");
        } catch (APIException e) {
            e.printStackTrace();
        }
        return null;
    }


}
