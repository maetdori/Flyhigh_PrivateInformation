package com.sample.postjson;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.view.View;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import android.util.Base64;
import java.util.concurrent.ExecutionException;

import org.json.JSONException;
import org.json.JSONObject;


import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManagerFactory;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static Context context;
    TextView tvIsConnected, tvResponse;
    EditText etServerURL;
    EditText etId;
    EditText etMsg;
    Button btnPost;
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode ==1 ) {
            if (grantResults.length > 0)
                grantResults[0] = PackageManager.PERMISSION_GRANTED;
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainActivity.context = getApplicationContext();
        setContentView(R.layout.activity_main);
        int permissionChceked = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
        if(permissionChceked != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_PHONE_STATE},1);
        }


        // get reference to the views
        tvIsConnected = (TextView) findViewById(R.id.tvIsConnected);
        etServerURL = findViewById(R.id.etServerUrl);
        etId = findViewById(R.id.etId);
        etMsg = (EditText) findViewById(R.id.etMsg);
        btnPost = (Button) findViewById(R.id.btnPost);
        tvResponse = (TextView) findViewById(R.id.tvResponse);



        // add click listener to Button "POST"
        btnPost.setOnClickListener(this);

    }
    @Override
    protected void onResume() {
        super.onResume();
        // check if you are connected or not
        if (isConnected()) {
            tvIsConnected.setBackgroundColor(0xFF00CC00);
            tvIsConnected.setText("You are conncted");
        } else {
            tvIsConnected.setBackgroundColor(Color.GREEN);
            tvIsConnected.setText("You are NOT conncted");
        }
    }

    public static String requestCert(String url) throws ProtocolException {
        HttpsURLConnection httpCon = setConnection(url);
        //getAndroidID
        TelephonyManager tm =(TelephonyManager)
                MainActivity.context.getSystemService(Context.TELEPHONY_SERVICE);
        String androidId = Settings.Secure.getString
                (MainActivity.context.getContentResolver(),Settings.Secure.ANDROID_ID);

        //setHttpMethod
        httpCon.setRequestMethod("GET");
        //setHeaders
        httpCon.setRequestProperty("Server-Cert-Id","hash");
        httpCon.setRequestProperty("Device-id",androidId);
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
    public static String SSLPOST(String url,String id ,String msg) throws JSONException, IOException {
        String result = null;
        HttpsURLConnection httpCon = setConnection(url);
        String json = "";

        // build jsonObject
        JSONObject jsonObject = new JSONObject();
        jsonObject.accumulate("id",id);
        jsonObject.accumulate("message", msg );

        // convert JSONObject to JSON to String
        json = jsonObject.toString();

        //getAndroidID
        TelephonyManager tm =(TelephonyManager)
                MainActivity.context.getSystemService(Context.TELEPHONY_SERVICE);
        String androidId = Settings.Secure.getString
                (MainActivity.context.getContentResolver(),Settings.Secure.ANDROID_ID);

        //setHttpMethod
        httpCon.setRequestMethod("POST");
        //setHeaders
        httpCon.setRequestProperty("Content-type", "application/json");
        httpCon.setRequestProperty("Server-Cert-Id","hash");
        httpCon.setRequestProperty("Device-id",androidId);

        // OutputStream으로 POST 데이터를 넘겨주겠다는 옵션.
        httpCon.setDoOutput(true);

        // InputStream으로 서버로 부터 응답을 받겠다는 옵션.
        httpCon.setDoInput(true);

        InputStream is = null;
        OutputStream os = httpCon.getOutputStream();
        os.write(json.getBytes("utf-8"));
        os.flush();
        // receive response as inputStream
        Certificate[] serverCert = httpCon.getServerCertificates();
        Log.d("POST","" + serverCert.length);
        try {
            is = httpCon.getInputStream();
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
    private static HttpsURLConnection setConnection(String url) {
        SSLContext context = null;

        try {
            context = cert(MainActivity.context.getFilesDir().getAbsolutePath() + "/cert.cer");
            URL urlCon = new URL(url);
            HttpsURLConnection httpCon = (HttpsURLConnection) urlCon.openConnection();
            httpCon.setSSLSocketFactory(context.getSocketFactory());
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
    private static SSLContext cert(String certPath) throws CertificateException, IOException, KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
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
    public boolean isConnected(){
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }
    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.btnPost:
                if(!validate())
                    Toast.makeText(getBaseContext(), "Enter some data!", Toast.LENGTH_LONG)
                            .show();
                else {
                    // call AsynTask to perform network operation on separate thread
                    HttpAsyncTask httpTask = new HttpAsyncTask(MainActivity.this,0);
                    // SERVERURL:연결할 서버
                    try {
                        String strJson = httpTask.execute("https://192.168.10.204:8080/api/insert",
                                etId.getText().toString(),etMsg.getText().toString()).get();
                        Toast.makeText(this, "Received!", Toast.LENGTH_LONG).show();
                        try {
                            Log.d("strJson",strJson);
                            JSONObject json = new JSONObject(strJson);
                            //String pubKey = json.get("P").toString();
                            tvResponse.setText(json.toString(1));
                        } catch (JSONException e) {
                            e.printStackTrace();
                            tvResponse.setText("ERR");
                        }
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.btnGetCert:
                HttpAsyncTask httpTask = new HttpAsyncTask(MainActivity.this,1);
                // SERVERURL:연결할 서버
                try {
                    String strJson = httpTask.execute("https://192.168.10.204:8080/api/getCert").get();
                    Toast.makeText(this, "Received!", Toast.LENGTH_LONG).show();
                    Log.d("strJson",strJson);
                    JSONObject json = new JSONObject(strJson);
                    String cert = json.get("Cert-base64").toString();
                    byte[] certContent = Base64.decode(cert,0);
                    Log.d("ahhhh","ahhhhh");
                    FileOutputStream out = new FileOutputStream(this.getFilesDir() +
                            File.separator +"pub.cer");
                    out.write(certContent);
                    tvResponse.setText(json.toString(1));
                } catch (JSONException e) {
                    e.printStackTrace();
                    tvResponse.setText("ERR");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    tvResponse.setText("ERR");
                } catch (IOException e) {
                    e.printStackTrace();
                    tvResponse.setText("ERR");
                } catch (ExecutionException e) {
                e.printStackTrace();
                tvResponse.setText("ERR");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    tvResponse.setText("ERR");
                }
                break;
        }

    }
    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        private MainActivity mainAct;
        private int mode;
        HttpAsyncTask(MainActivity mainActivity,int mode) {
            this.mainAct = mainActivity;
            this.mode = mode;
        }
        @Override
        protected String doInBackground(String... urls) {
            //urls[0] : url
            //urls[1~]: msg
            //return POST(urls[0],urls[1]);
            try {
                switch(mode) {
                    case 0:
                        return SSLPOST(urls[0], urls[1], urls[2]);
                    case 1:
                        return requestCert(urls[0]);
                }
            } catch (ProtocolException e) {
                e.printStackTrace();
                return "ERR";
            } catch (IOException e) {
                e.printStackTrace();
                return "ERR";
            } catch (JSONException e) {
                e.printStackTrace();
                return "ERR";
            }
            return "ERR";
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }
    }

    private boolean validate(){
        if(etMsg.getText().toString().trim().equals(""))
            return false;
        else
            return true;
    }
    private static String convertInputStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }
}
