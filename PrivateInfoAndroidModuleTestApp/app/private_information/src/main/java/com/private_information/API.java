package com.private_information;


import android.content.Context;

import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.security.GeneralSecurityException;

import java.security.InvalidKeyException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import java.security.NoSuchProviderException;
import java.security.Principal;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import android.util.Base64;


import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import android.util.Log;


import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManagerFactory;
public class API {
    static final int TYPE_WIFI = 1;
    static final int TYPE_MOBILE = 2;
    static final int TYPE_NOT_CONNECTED = 3;
    private static final int CONNECTION_TIMEOUT = 1000; // 서버의 응답을 기다리는 최대 시간
    private static final int READ_TIMEOUT = 1000; // 서버의 응답을 읽는 최대 시간
    private static final String CERTPATH = "/cert.cer";

    //네트워크 연결 여부 반환
    static private int getConnectivityStatus(Context context) { //해당 context의 서비스를 사용하기위해서 context객체를 받는다.
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

    //inputstream에서 string 읽기
    private static String convertInputStreamToString(InputStream inputStream) throws APIException{
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";
            String result = "";
            while ((line = bufferedReader.readLine()) != null)
                result += line;

            inputStream.close();
            return result;
        }catch (IOException e) {
            throw new APIException("BufferedReader can't read line",APIException.CONV_IS_TO_STR | APIException.IOEXCEPTION,e);
        }

    }

    //미리 설치한 서버 인증서를 신뢰하는 ca로 등록 후 SSLContext생성
    private static SSLContext cert(String certPath) throws APIException {
        // Load CAs from an InputStream
        // (could be from a resource or ByteArrayInputStream or ...)
        Certificate ca;
        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            InputStream caInput = new BufferedInputStream(new FileInputStream(certPath));

            try {
                ca = cf.generateCertificate(caInput);
                Log.d("cert", "ca=" + ((X509Certificate) ca).getSubjectDN());
            } finally {
                caInput.close();
            }
        } catch(CertificateException e) {
            throw new APIException("Invalid certificate format",APIException.CERT | APIException.INV_CERT_FORMAT,e);
        } catch (IOException e) {
            throw new APIException("Can't read file",APIException.CERT | APIException.IOEXCEPTION,e);
        }

        // Create a KeyStore containing our trusted CAs
        KeyStore keyStore;
        try {
            String keyStoreType = KeyStore.getDefaultType();
            keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);
        }catch (IOException | CertificateException | NoSuchAlgorithmException | KeyStoreException e) {
            throw new APIException("Can't create Keystore with given certification",
                    APIException.CERT | APIException.CREATE_KS_ERROR,e);
        }

        // Create a TrustManager that trusts the CAs in our KeyStore
        TrustManagerFactory tmf;
        try {
            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keyStore);
        } catch (NoSuchAlgorithmException | KeyStoreException e) {
            throw new APIException("Can't create TrustManager with given certification",
                    APIException.CERT | APIException.CREATE_TMF_ERROR,e);
        }

        // Create an SSLContext that uses our TrustManager
        SSLContext context;
        try {
            context = SSLContext.getInstance("TLS");
            context.init(null, tmf.getTrustManagers(), null);
            return context;
        } catch (KeyManagementException | NoSuchAlgorithmException e) {
            throw new APIException("Can't create SSLContext with given TrustManager",
                    APIException.CERT | APIException.CREATE_SSL_ERROR,e);
        }

    }

    //서버와 연결 수립. 사설인증서로 연결되지 않을경우, 사용하지 않고 다시연결. 또 안되면 예외 발생
    private static HttpsURLConnection setConnection(Context context,String url) throws APIException {
        SSLContext sslContext = cert(context.getFilesDir().getAbsolutePath() + CERTPATH);
        try {
            URL urlCon = new URL(url);
            HttpsURLConnection httpCon = (HttpsURLConnection) urlCon.openConnection();
            httpCon.setSSLSocketFactory(sslContext.getSocketFactory());
            httpCon.setHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;//default가 false 입니다.
                }
            });
            httpCon.setConnectTimeout(CONNECTION_TIMEOUT);
            httpCon.setReadTimeout(READ_TIMEOUT);
            httpCon.connect();
            //connect에서 오류나면 연결 실패로 판단

            urlCon = new URL(url);
            httpCon = (HttpsURLConnection) urlCon.openConnection();
            httpCon.setSSLSocketFactory(sslContext.getSocketFactory());
            httpCon.setHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;//default가 false 입니다.
                }
            });
            httpCon.setConnectTimeout(CONNECTION_TIMEOUT);
            httpCon.setReadTimeout(READ_TIMEOUT);
            return httpCon;
        } catch (Exception e) { // sslContext를 이용한 연결 실패시 사용하지 않고 재시도
            e.printStackTrace();
            Log.e("set Connection","reconnect");
            URL urlCon = null;
            try {
                urlCon = new URL(url);
                HttpsURLConnection httpCon = (HttpsURLConnection) urlCon.openConnection();
                httpCon.setConnectTimeout(CONNECTION_TIMEOUT);
                httpCon.setReadTimeout(READ_TIMEOUT);
                return httpCon;
            } catch (IOException ex) {
                throw new APIException("Can't openConnection to url :" + url,APIException.SET_CONNECTION | APIException.INV_SERVERURL,ex);
            }
        }
    }

    private static void setHeader(Context context,HttpsURLConnection httpCon,String url) throws APIException {
        HttpsURLConnection dummy = setConnection(context,url);
        try {
            dummy.connect();
        } catch (IOException e) {
            throw new APIException("Can't establish Connection to Server",APIException.SET_HEADER | APIException.SERVER_NOT_RESPONDING ,e);
        }

        //getAndroidID
        String androidId = getSSAID(context);
        //getServerCert
        X509Certificate cert = null;
        try {
            cert = (X509Certificate) dummy.getServerCertificates()[0];
        } catch (SSLPeerUnverifiedException e) {
            throw new APIException("Can't get SSL Certificate From Server",APIException.SET_HEADER | APIException.SSL_PEER_UNVERIFIED,e);
        }
        //setHeaders
        httpCon.setRequestProperty("Content-Type", "application/json");
        httpCon.setRequestProperty("Device-Id", androidId);
        httpCon.setRequestProperty("Server-Cert-Id", Base64.encodeToString(
                cert.getSignature(), Base64.NO_WRAP));

        Log.i("SSAID",androidId);

    }

    //암호화에 사용할 publickey가 담긴 인증서를 base64로 받기
    private static String requestCertBase64(Context context, String url) throws APIException {
        HttpsURLConnection httpCon = setConnection(context,url);

        //setHttpMethod
        try {
            httpCon.setRequestMethod("GET");
        } catch (ProtocolException e) {
            e.printStackTrace();
        }
        //setHeaders
        setHeader(context,httpCon,url);
        // OutputStream으로 POST 데이터를 넘겨주겠다는 옵션.
        httpCon.setDoOutput(false);

        // InputStream으로 서버로 부터 응답을 받겠다는 옵션.
        httpCon.setDoInput(true);
        String result = null;
        try {
            InputStream is = httpCon.getInputStream();
            // convert inputstream to string
            if(is == null)
                throw new APIException("Can not get InputStream from Server",
                        APIException.REQ_CERT_B64 | APIException.NULL_INPUTSTREAM);
            result = convertInputStreamToString(is);
        } catch (IOException e) {
            throw new APIException("Can not get InputStream from Server",
                    APIException.REQ_CERT_B64 | APIException.NULL_INPUTSTREAM,e);
        } finally {
            httpCon.disconnect();
        }
        return result;
    }

    //메소드명이 포함된 url에서 hostname을 return
    private static String getHost(String url) {
        int pos = url.indexOf("/");
        String tmp = url.substring(pos + 2);
        pos += tmp.indexOf("/");
        pos += 2;
        String host = url.substring(0,pos);
        return host;
    }

    //requestCertbase64를 호출해 base64데이터를 받고 거기서 인증서 추출
    private static X509Certificate getCert(Context context,String url) throws APIException{
        X509Certificate certificate = null;

        String host = getHost(url);
        try {
            JSONObject json = new JSONObject(requestCertBase64(context,host + "/private/getCert"));
            String cert_base64 = json.getString("cert_base64");
            Log.d("getCert","cert_base64 : " + cert_base64);
            byte[] input = Base64.decode(cert_base64,Base64.NO_WRAP);
            InputStream is = new ByteArrayInputStream(input);
            certificate = (X509Certificate) CertificateFactory.getInstance("X509").generateCertificate(is);
        } catch (JSONException e) {
            throw new APIException("Can't get Certification From Json",APIException.GET_CERT | APIException.JSON_GET_ERR,e);
        } catch (CertificateException e) {
            e.printStackTrace();
        }
        return certificate;
    }


    //url로 주어진 post메소드 실행. 키교환 후 데이터를 보낼 때 암호화, 받고나서 복호화
    private static String POSTSSL(Context context, String url,String... args)
            throws APIException {
        if(getConnectivityStatus(context) == TYPE_NOT_CONNECTED)
            throw new APIException("NO Internet",APIException.NO_INTERNET);

        Log.i("POSTSSL", "ServerUrl : " + url);
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
        byte[] ecKey = new byte[0];
        try {
            ecKey = RSAModule.encryptRSA(pubKey.getEncoded(), cKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            // build jsonObject
            int pos = url.lastIndexOf("/");
            String ext = url.substring(pos + 1);
            jsonObject.accumulate("pubKey", Base64.encodeToString(pubKey.getEncoded(),
                    Base64.NO_WRAP));
            jsonObject.accumulate("cKey", Base64.encodeToString(ecKey, Base64.NO_WRAP));
            if (ext.equals("test")) {
                //use for test
                ;
            } else if (ext.equals("getList")) {
                if(args == null || args[0] == null) {
                    throw new APIException("Invalid arguments for getList",APIException.POSTSSL | APIException.INV_ARGS);
                }
                jsonObject.accumulate("username", args[0]);
            } else if (ext.equals("getInfo")) {
                if(args == null || args.length < 4 || args[0] == null || args[1] == null || args[2] == null || args[3] == null) {
                    throw new APIException("Invalid arguments for getList",APIException.POSTSSL | APIException.INV_ARGS);
                }
                jsonObject.accumulate("username", args[0]);
                jsonObject.accumulate("subject", args[1]);
                JSONObject validity = new JSONObject();
                validity.accumulate("notBefore", args[2]);
                validity.accumulate("notAfter", args[3]);
                jsonObject.accumulate("validity", validity);
            } else {
                throw new APIException("Invalid POST method",APIException.POSTSSL | APIException.INV_METHOD_URL);
            }
            // convert JSONObject to JSON to String
            json = jsonObject.toString();
            Log.d("POSTSSL", "RequestBody : " + json);



            httpCon.setRequestMethod("POST");
            //setHeaders
            setHeader(context,httpCon,url);

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
                if(is == null)
                    throw new APIException("Can not get InputStream from Server",
                            APIException.POSTSSL | APIException.NULL_INPUTSTREAM);
                result = convertInputStreamToString(is);
            } catch (IOException e) {
                throw new APIException("Can not get InputStream from Server",
                        APIException.POSTSSL | APIException.NULL_INPUTSTREAM,e);
            } finally {
                httpCon.disconnect();
            }
            Log.i("RESPONSE JSON", "result : " + result);
            if (result == null) return "null";
            JSONObject responseJSON = new JSONObject(result);
            try {
                responseJSON.get("code");
                return responseJSON.toString();
            } catch(JSONException e){
                ;
            }

            JSONArray encryptedListJSON = null;
            try {
                encryptedListJSON = (JSONArray) responseJSON.get("encryptedElements");
            } catch (JSONException e) {
                return responseJSON.toString();
            }
            List<String> encList = new ArrayList<>();
            for (int i = 0; i < encryptedListJSON.length(); i++) {
                encList.add(encryptedListJSON.getJSONObject(i).getString("name"));
            }
            Log.d("ckeyBase64", Base64.encodeToString(cKey, Base64.NO_WRAP));
            byte[] key = new byte[32];
            System.arraycopy(cKey, 0, key, 0, 16);
            byte[] sKey = Base64.decode(responseJSON.getString("sKey"), Base64.NO_WRAP);
            sKey = RSAModule.decryptRSA(pubKey.getEncoded(), sKey);
            Log.d("skeyBase64", Base64.encodeToString(sKey, Base64.NO_WRAP));
            System.arraycopy(sKey, 0, key, 16, 16);
            Log.d("keyBase64", Base64.encodeToString(key, Base64.NO_WRAP));
            AES256Util aes = new AES256Util(key);

            for (String s : encList) {
                Log.d("ENCLIST", s);
                JsonDecryptModule.Decrypt(s, responseJSON, aes);
            }
            responseJSON.remove("encryptedElements");
            responseJSON.remove("sKey");
            result = responseJSON.toString(1);
            return result;
        } catch (JSONException e) {
            throw new APIException("Can't get Json From Response",APIException.POSTSSL | APIException.JSON_GET_ERR,e);
        } catch (IOException e) {
            throw new APIException("Can't get Json From Response",APIException.POSTSSL | APIException.JSON_GET_ERR,e);
        }
    }



    /*인터넷에 연결되어 있을경우 postssl로 데이터 받아옴. 네트워크가 없을 경우 로컬데이터베이스에서 받아옴
     *로컬데이터베이스도 비어있을 경우 에러
     * */

    public static String getListPrivateInformation(Context context,String url,String username) throws APIException {
        String ret = null;
        DatabaseManager db = DatabaseManager.getInstance(context);
        try {
            ret = POSTSSL(context,url + "/private/getList",username,null);
        } catch (Exception e) {
            Log.e("API","getListPrivateInformation : getList from Android Database");
            e.printStackTrace();
            return attachErrorMessage(db.getList(),e);
        }

        try {
            JSONObject tmp = new JSONObject(ret);
            tmp.get("code");
            return ret;
        } catch (JSONException e) {
            ;
        }

        db.updateList(ret);
        return ret;
    }
    public static String getPrivateInformation(Context context, String url, String username,
                                               String subject, String notBefore, String notAfter) throws APIException {
        String ret = null;
        DatabaseManager db = DatabaseManager.getInstance(context);
        try {
            ret = POSTSSL(context,url + "/private/getInfo",username,subject,notBefore,notAfter);
        }catch(Exception e) {
            Log.e("API","getPrivateInformation : getList from Android Database");
            e.printStackTrace();

            return attachErrorMessage(db.searchByNameFromInfo(subject),e);
        }
        try {
            JSONObject tmp = new JSONObject(ret);
            tmp.get("code");
            return ret;
        } catch (JSONException e) {
            ;
        }
        db.updatePrivateInformation(ret,subject);
        return ret;
    }

    public static String getNpki(Context context,String name) throws APIException {
        DatabaseManager db = DatabaseManager.getInstance(context);
        return db.getNpki(name);
    }

    public static String test(Context context, String url)  {
        return null;
    }
    public static String getSSAID(Context context) {
        return Settings.Secure.getString
                (context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }
    private static String attachErrorMessage(String s,Exception e) {
        JSONObject json = null;
        try {
            json = new JSONObject(s);
        } catch (JSONException ee) {
            ee.printStackTrace();
        }
        JSONObject exception = new JSONObject();

        try {
            if(e instanceof APIException)
                exception.accumulate("code",((APIException) e).getCode());
            else
                exception.accumulate("code",APIException.API);
            exception.accumulate("message",e.getMessage());
            json.accumulate("Exception",exception);
        } catch (JSONException ex) {
            ex.printStackTrace();
        }

        return json.toString();
    }



}
