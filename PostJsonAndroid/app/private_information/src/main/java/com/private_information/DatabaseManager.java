package com.private_information;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

class DatabaseManager {
    static final String DB_NAME = "PrivateInfromantion";
    static final String TABLE_LIST = "LIST";
    static final String TABLE_INFO = "INFO";
    static final String TABLE_SITE = "SITE";
    static final String COL_SITE = "site";
    static final String COL_ID = "id";
    static final String COL_PW = "pw";
    private Context myContext;
    private static DatabaseManager myDBManager = null;
    private SQLiteDatabase myDatabase = null;
    static DatabaseManager getInstance(Context context) {
        if(myDBManager == null) {
            myDBManager = new DatabaseManager(context);
        }
        return myDBManager;
    }
    private DatabaseManager(Context context) {
        myContext = context;

        //DB Open
        myDatabase = context.openOrCreateDatabase(DB_NAME, context.MODE_PRIVATE,null);

        //Table 생성
        myDatabase.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_LIST+
                "(co_name" + " VARCHAR(50) PRIMARY KEY," +
                "issuer VARCHAR(200)," +
                "notBefore VARCHAR(8)," +
                "notAfter VARCHAR(8));");


        myDatabase.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_INFO+
                "(" +
                "co_name VARCHAR(50) PRIMARY KEY," +
                "cert_pw VARCHAR(50)," +
                "notBefore VARCHAR(8)," +
                "notAfter VARCHAR(8)," +
                "co_cert_der VARCHAR(2560)," +
                "co_cert_key VARCHAR(2560)," +
                "co_certification VARCHAR(4096)" +
                ");");

        myDatabase.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_SITE+
                "(" +
                "co_name VARCHAR(50) PRIMARY KEY," +
                "site VARCHAR(50)," +
                "id VARCHAR(50)," +
                "pw VARCHAR(50)" +
                ");");
    }
    String getList() {
        Cursor cur = myDatabase.rawQuery("SELECT * FROM " + TABLE_LIST + ";",null);
        JSONObject obj = new JSONObject();
        JSONArray arr = new JSONArray();
        while(cur.moveToNext()) {
            String co_name = cur.getString(0);
            String issuer= cur.getString(1);
            String notBefore= cur.getString(2);
            String notAfter= cur.getString(3);
            int cert_type = cur.getInt(4);
            JSONObject element = new JSONObject();
            try {
                element.accumulate("co_name",co_name);
                element.accumulate("issuer",issuer);
                element.accumulate("notBefore",notBefore);
                element.accumulate("notAfter",notAfter);
                element.accumulate("cert_type",cert_type);
                element.accumulate("cert_")
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
            arr.put(element);
        }
        try {
            obj.accumulate("privateInfoList",arr);
            return obj.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
    String searchByNameFromInfo(String name) {
        Cursor cur = myDatabase.rawQuery("SELECT * FROM " + TABLE_LIST +" WHERE co_name = '"
                + name +"';",null);
        JSONObject json = new JSONObject();
        if(!cur.moveToNext()) {
            new Exception("No such name").printStackTrace();
            return null;
        }
        try {
            json.accumulate("cert_pw",cur.getString(1));
            int type = cur.getInt()
            json.accumulate("co_cert_")
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        cur = myDatabase.rawQuery("SELECT * FROM " + TABLE_SITE + " WHERE co_name = "+ name +";"
                ,null);
        int cnt = 0;
        JSONArray account = new JSONArray();
        while(cur.moveToNext()) {
            JSONObject accountInfo = new JSONObject();
            try {
                accountInfo.accumulate("site",cur.getString(1));
                accountInfo.accumulate("id",cur.getString(2));
                accountInfo.accumulate("pw",cur.getString(3));
                account.put(accountInfo);
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }
        try {
            if(account.length() != 0)
            json.accumulate("account",account);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return json.toString();
    }
    void updatePrivateInformation(String source,String co_name) throws APIException {
        /*JSONObject json = null;
        try {
            json = new JSONObject(source);
        } catch (JSONException e) {
            throw new APIException("Could not create JSON",APIException.UPDATEPRIVATEINFO |
                    APIException.JSON_SOURCE_ERR,e);
        }
        try {
            String cert_pw = json.getString("cert_pw");
            JSONArray account = (JSONArray) json.get("account");
            for(int i = 0 ; i <account.length();i++) {
                JSONObject acc = account.getJSONObject(i);
                ContentValues vals = new ContentValues();
                vals.put("co_name",co_name);
                vals.put("site",acc.getString("site"));
                vals.put("id",acc.getString("id"));
                vals.put("pw",acc.getString("pw"));
                myDatabase.insertOrThrow(TABLE_SITE,null,vals);
            }
            X509Certificate cert = Certificate.
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }
         */

    }
}
