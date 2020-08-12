package com.sample.postjson;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ParseException;
import android.os.Bundle;
import android.view.View;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.GeneralSecurityException;

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

import com.private_information.API;
import com.private_information.APIException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static String SERVER_URL = "https://ec2-13-124-68-42.ap-northeast-2.compute.amazonaws.com:80";
    private static Context context;
    TextView tvIsConnected, tvResponse;
    EditText etServerURL;
    EditText etUsername;
    EditText etSubject;
    EditText etNotBefore;
    EditText etNotAfter;
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
        etUsername = findViewById(R.id.etUsername);
        etSubject = (EditText) findViewById(R.id.etSubject);
        etNotBefore = (EditText) findViewById(R.id.etNotBefore);
        etNotAfter = (EditText) findViewById(R.id.etNotAfter);
        tvResponse = (TextView) findViewById(R.id.tvResponse);
        // add click listener to Button "POST"
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
            case R.id.btnGetList:
                if(!validate())
                    Toast.makeText(getBaseContext(), "Enter some data!", Toast.LENGTH_LONG)
                            .show();
                else {
                    // call AsynTask to perform network operation on separate thread
                    getListTask httpTask = new getListTask(MainActivity.this);
                    // SERVERURL:연결할 서버
                    try {
                        String strJson = httpTask.execute(etServerURL.getText().toString(),
                                etUsername.getText().toString()).get();
                        Log.d("MAIN:onClick","strJson : " +strJson );
                        Toast.makeText(this, "Received!", Toast.LENGTH_LONG).show();
                        try {
                            Log.d("strJson",strJson);
                            JSONObject json = new JSONObject(strJson);
                            //String pubKey = json.get("P").toString();
                            tvResponse.setText(json.toString(1));
                        } catch (JSONException e) {
                            e.printStackTrace();
                            tvResponse.setText(strJson);
                        }
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.btnGetInfo:
                if(!validate())
                    Toast.makeText(getBaseContext(), "Enter some data!", Toast.LENGTH_LONG)
                            .show();
                else {
                    // call AsynTask to perform network operation on separate thread
                    getInfoTask httpTask = new getInfoTask(MainActivity.this);
                    // SERVERURL:연결할 서버
                    try {
                        String strJson = httpTask.execute(etServerURL.getText().toString(),
                                etUsername.getText().toString(),etSubject.getText().toString(),
                                etNotBefore.getText().toString(),etNotAfter.getText().toString()).get();
                        Toast.makeText(this, "Received!", Toast.LENGTH_LONG).show();
                        try {
                            Log.d("strJson",strJson);
                            JSONObject json = new JSONObject(strJson);
                            //String pubKey = json.get("P").toString();
                            tvResponse.setText(json.toString(1));
                        } catch (JSONException e) {
                            e.printStackTrace();
                            tvResponse.setText(strJson);
                        }
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.btnTest:
               HttpAsyncTask httpTask = new HttpAsyncTask(MainActivity.this);
                // SERVERURL:연결할 서버
                try {
                    String strJson = httpTask.execute(etServerURL.getText().toString()).get();
                    Toast.makeText(this, "Received!", Toast.LENGTH_LONG).show();
                    try {
                        Log.d("strJson",strJson);
                        JSONObject json = new JSONObject(strJson);
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
                return API.test(mainAct,urls[0]);
            } catch (Exception e) {
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
    private class getListTask extends AsyncTask<String, Void, String> {
        private MainActivity mainAct;
        getListTask(MainActivity mainActivity) {
            this.mainAct = mainActivity;
        }
        @Override
        protected String doInBackground(String... urls) {
            //urls[0] : url
            //urls[1~]: msg
            //return POST(urls[0],urls[1]);
            try {
                return API.getListPrivateInformation(mainAct,urls[0], urls[1]);
            } catch (Exception e) {
                StringWriter sw = new StringWriter();
                e.printStackTrace(new PrintWriter(sw));
                String exceptionAsString = sw.toString();
                e.printStackTrace();
                return exceptionAsString;
            }

        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }
    }
    private class getInfoTask extends AsyncTask<String, Void, String> {
        private MainActivity mainAct;
        getInfoTask(MainActivity mainActivity) {
            this.mainAct = mainActivity;
        }
        @Override
        protected String doInBackground(String... urls) {
            //urls[0] : url
            //urls[1~]: msg
            //return POST(urls[0],urls[1]);
            try {
                return API.getPrivateInformation(mainAct,urls[0], urls[1],urls[2],urls[3],urls[4]);
            } catch (Exception e) {
                StringWriter sw = new StringWriter();
                e.printStackTrace(new PrintWriter(sw));
                String exceptionAsString = sw.toString();
                e.printStackTrace();
                return exceptionAsString;
            }
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }
    }

    private boolean validate(){
        if(etUsername.getText().toString().trim().equals(""))
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
