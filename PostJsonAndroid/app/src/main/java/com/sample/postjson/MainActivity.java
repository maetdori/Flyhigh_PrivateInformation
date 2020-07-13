package com.sample.postjson;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ParseException;
import android.os.Bundle;
import android.provider.Settings;
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
import java.net.ProtocolException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import android.util.Base64;

import java.security.spec.InvalidKeySpecException;
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

import com.example.module.API;
import com.example.module.crypto.RSAModule;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManagerFactory;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static String SERVER_URL = "https://www.google.com:443";
    private static Context context;
    TextView tvIsConnected, tvResponse;
    EditText etServerURL;
    EditText etId;
    EditText etMsg;
    Button btnPost;
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
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
        int permissionChceked = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_PHONE_STATE);
        if(permissionChceked != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]
                    {Manifest.permission.READ_PHONE_STATE},1);
        }
        // get reference to the views
        tvIsConnected = (TextView) findViewById(R.id.tvIsConnected);
        etServerURL = findViewById(R.id.etServerUrl);
        etId = findViewById(R.id.etUsername);
        etMsg = (EditText) findViewById(R.id.etSubject);
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
                    HttpAsyncTask httpTask = new HttpAsyncTask(MainActivity.this);
                    // SERVERURL:연결할 서버
                    try {
                        String strJson = httpTask.execute(SERVER_URL,
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
                HttpAsyncTask httpTask = new HttpAsyncTask(MainActivity.this);
                // SERVERURL:연결할 서버
                try {
                    String strJson = httpTask.execute(SERVER_URL + "/api/getCert").get();
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
        HttpAsyncTask(MainActivity mainActivity) {
            this.mainAct = mainActivity;
        }
        @Override
        protected String doInBackground(String... urls) {
            //urls[0] : url
            //urls[1~]: msg
            //return POST(urls[0],urls[1]);
            try {
                return API.POSTSSL(mainAct,urls[0], urls[1],urls[2]);
            }catch (ParseException e) {
                e.printStackTrace();
                return "ERR";
            } catch (GeneralSecurityException e) {
                e.printStackTrace();
                return "ERR";
            } catch (IOException e) {
                e.printStackTrace();
                return "ERR";
            } catch (JSONException e) {
                e.printStackTrace();
                return "ERR";
            }
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
