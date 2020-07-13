package com.example.module;


import android.content.Context;

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
import java.util.Date;
import java.util.List;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import android.util.Log;


import com.example.module.crypto.AES128Util;
import com.example.module.crypto.RSAModule;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManagerFactory;
public class API {
    private static String convertInputStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }
    private static SSLContext cert(String certPath) throws CertificateException, IOException,
            KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
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
            return httpCon;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    private static String requestCert(Context context, String url) throws ProtocolException {
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
    public static String POSTSSL(Context context, String url,String id ,String msg)
            throws JSONException, IOException, GeneralSecurityException {
        X509Certificate certificate = null;
        String result = null;
        String host = getHost(url);
        result = requestCert(context,host + "/api/getCert");
        try {
            FileInputStream is = new FileInputStream(context.getFilesDir()
                    + File.separator + "/pub.cer");
            certificate = (X509Certificate) CertificateFactory.getInstance("X509")
                    .generateCertificate(is);
        } catch (IOException | CertificateException e) {
            e.printStackTrace();
        }
        if (certificate == null) {
            Log.e("MODULE.API.POSTSSL","CERTIFICATE IS NULL");
            return null;
        } else if (certificate.getNotAfter().getTime() < System.currentTimeMillis()) {
            Log.e("MODULE.API.POSTSSL","CERTIFICATE IS EXPIRED");
            return null;
        }
        int pos = url.lastIndexOf("/");
        String ext = url.substring(pos + 1);
        if(ext.equals("getCert"))
            return result;

        HttpsURLConnection httpCon = setConnection(context,url);
        String json = "";

        PublicKey pubKey = certificate.getPublicKey();
        SecureRandom rand = new SecureRandom();
        byte[] cKey = new byte[16];
        rand.nextBytes(cKey);
        byte[] ecKey = RSAModule.encryptRSA(pubKey.getEncoded(), cKey);
        // build jsonObject

        JSONObject jsonObject = new JSONObject();
        if (ext.equals("test")) {
            jsonObject.accumulate("pubKey",Base64.encodeToString(pubKey.getEncoded(),
                    Base64.NO_WRAP));
            jsonObject.accumulate("cKey", Base64.encodeToString(ecKey, Base64.NO_WRAP));
            jsonObject.accumulate("id",id);
            jsonObject.accumulate("message",msg);
        }
        else if(ext.equals("getInfo")) {
            jsonObject.accumulate("pubKey",Base64.encodeToString(pubKey.getEncoded(),
                    Base64.NO_WRAP));
            jsonObject.accumulate("cKey", Base64.encodeToString(ecKey, Base64.NO_WRAP));
            jsonObject.accumulate("id", id);
        }
        // convert JSONObject to JSON to String
        json = jsonObject.toString();

        //getAndroidID
        TelephonyManager tm = (TelephonyManager)
                context.getSystemService(Context.TELEPHONY_SERVICE);
        String androidId = Settings.Secure.getString
                (context.getContentResolver(), Settings.Secure.ANDROID_ID);

        String transactionTime = " ";

        //setHttpMethod
        httpCon.setRequestMethod("POST");
        //setHeaders
        httpCon.setRequestProperty("Content-type", "application/json");
        httpCon.setRequestProperty("Device-id", androidId);
        httpCon.setRequestProperty("TransactionTime",transactionTime);

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
        Log.i("RESPONSE JSON",result);
        JSONObject responseJSON = new JSONObject(result);
        JSONArray encryptedListJSON = (JSONArray) responseJSON.get("encryptedElements");
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
            String tmp = responseJSON.getString(s);
            responseJSON.remove(s);
            responseJSON.accumulate(s,aes.decrypt(Base64.decode(tmp.getBytes(),Base64.NO_WRAP)));
        }
        responseJSON.remove("encryptedElements");
        responseJSON.remove("sKey");
        result = responseJSON.toString(1);
        return result;
    }
    public static String getListPrivateInformation(Context context,String url,String username) throws IOException, GeneralSecurityException, JSONException {
        return POSTSSL(context,url + "/private/getList",username,null);
    }
    /*public static String getPrivateInformation(Context context, String url, String username,
                                               String subject, Date notBefore, Date NotAfter) {

    }*/


}
