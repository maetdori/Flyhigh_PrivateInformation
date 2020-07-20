package com.private_information;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


class DatabaseManager {
    //consts
    static final String DB_NAME = "PrivateInfromantion";
    static final String TABLE_LIST = "LIST";
    static final String TABLE_INFO = "INFO";
    static final String TABLE_SITE = "SITE";

    //fields
    private Context myContext;
    private static DatabaseManager myDBManager = null;
    private SQLiteDatabase myDatabase = null;

    //constructor
    private DatabaseManager(Context context) {
        myContext = context;

        //DB Open
        myDatabase = context.openOrCreateDatabase(DB_NAME, context.MODE_PRIVATE,null);

        //Table 생성
        myDatabase.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_LIST+
                "(co_name" + " VARCHAR(50) PRIMARY KEY," +
                "co_active_date VARCHAR(8)," +
                "co_exp_date VARCHAR(8));");


        myDatabase.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_INFO+
                "(" +
                "co_name VARCHAR(50) PRIMARY KEY," +
                "cert_pw VARCHAR(50)," +
                "co_cert_type INTEGER,"+
                "co_cert_der VARCHAR(2560)," +
                "co_cert_key VARCHAR(2560)," +
                "co_certification VARCHAR(4096)," +
                "co_count INTEGER" +
                ");");

        myDatabase.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_SITE+
                "(" +
                "co_name VARCHAR(50)," +
                "site VARCHAR(50)," +
                "id VARCHAR(50)," +
                "pw VARCHAR(50)" +
                ");");
    }

    static DatabaseManager getInstance(Context context) {
        if(myDBManager == null) {
            myDBManager = new DatabaseManager(context);
        }
        return myDBManager;
    }

    private APIException nullCheck(String name,String s) {
        if(s == null) {
            return new APIException(name +" is null",APIException.SEARCH_FROM_PRIVATEINFO |
                    APIException.REQUIRED_VALUE_NULL);
        }
        return null;
    }

    String getList(String username) throws APIException {
        Cursor cur = myDatabase.rawQuery("SELECT * FROM " + TABLE_LIST + ";", null);
        JSONObject obj = new JSONObject();
        int count = 0;
        JSONArray accountArr = new JSONArray();
        while (cur.moveToNext()) {
            count++;
            String co_name = cur.getString(0);
            String notBefore = cur.getString(1);
            String notAfter = cur.getString(2);
            String[] names = new String[]{"co_name", "notBefore", "notAfter"};
            String[] vals = new String[]{co_name, notBefore, notAfter};
            for (int i = 0; i < names.length; i++) {
                APIException e;
                if ((e = nullCheck(names[i], vals[i])) != null) {
                    throw e;
                }
            }
            Log.i("DatabaseManager", "subject : " + co_name);
            Log.i("DatabaseManager", "notBefore : " + notBefore);
            Log.i("DatabaseManager", "notAfter : " + notAfter);
            JSONObject account = new JSONObject();
            try {
                account.accumulate("subject", co_name);
                JSONObject validity = new JSONObject();
                validity.accumulate("notBefore", notBefore);
                validity.accumulate("notAfter", notAfter);
                account.accumulate("validity", validity);
                accountArr.put(account);
            } catch (JSONException e) {
                throw new APIException("error while building JSON", APIException.GET_LIST |
                        APIException.JSON_BUILD_ERR, e);
            }
        }
        if(count == 0) {
            throw new APIException("PrivateInfoList is Empty", APIException.GET_LIST |
                    APIException.DB_NO_DATA);
        }
        try {
            obj.accumulate("count", count);
            obj.accumulate("account", accountArr);
            return obj.toString();
        } catch (JSONException e) {
            throw new APIException("error while building JSON", APIException.GET_LIST |
                    APIException.JSON_BUILD_ERR, e);
        }
    }
    String searchByNameFromInfo(String name) throws APIException {
        Cursor cur = myDatabase.rawQuery("SELECT * FROM " + TABLE_INFO + " WHERE co_name = '"
                + name + "';", null);
        JSONObject json = new JSONObject();
        if (!cur.moveToNext()) {
            throw new APIException("No such name in local Database", APIException.SEARCH_FROM_PRIVATEINFO |
                    APIException.NO_SUCH_NAME);
        }
        try {
            String cert_pw = cur.getString(1);
            int cert_type = cur.getInt(2);
            String cert_der = cur.getString(3);
            String cert_key = cur.getString(4);
            String cert_pfx = cur.getString(5);
            //check input
            if (cert_type != 1 && cert_type != 2) {
                throw new APIException("cert_type is wrong", APIException.SEARCH_FROM_PRIVATEINFO |
                        APIException.REQUIRED_VALUE_NULL);
            }
            APIException e = null;
            if ((e = nullCheck("cert_pw", cert_pw)) != null)
                throw e;
            if (cert_type == 1) {
                if ((e = nullCheck("cert_der", cert_der)) != null)
                    throw e;
                if ((e = nullCheck("cert_key", cert_key)) != null)
                    throw e;
            } else {
                if ((e = nullCheck("cert_pfx", cert_pfx)) != null)
                    throw e;
            }

            json.accumulate("cert_pw", cert_pw);
            json.accumulate("cert_type", cert_type);
            JSONObject certification = new JSONObject();
            certification.accumulate("der", cert_der);
            certification.accumulate("key", cert_key);
            certification.accumulate("pfx", cert_pfx);
            json.accumulate("certification", certification);

            Log.i("DatabaseManager", "cert_pw : " + cert_pw);
            Log.i("DatabaseManager", "cert_type : " + cert_type);
            Log.i("DatabaseManager", "cert_der : " + cert_der);
            Log.i("DatabaseManager", "cert_key : " + cert_key);
            Log.i("DatabaseManager", "cert_pfx : " + cert_pfx);
        } catch (JSONException e) {
            throw new APIException("error while building JSON", APIException.SEARCH_FROM_PRIVATEINFO |
                    APIException.JSON_BUILD_ERR, e);
        }
        cur = myDatabase.rawQuery("SELECT * FROM " + TABLE_SITE + " WHERE co_name = " + name + ";"
                , null);
        int cnt = 0;
        JSONArray account = new JSONArray();
        while (cur.moveToNext()) {
            cnt++;
            JSONObject accountInfo = new JSONObject();
            try {
                String site = cur.getString(1);
                String id = cur.getString(2);
                String pw = cur.getString(3);
                accountInfo.accumulate("site", site);
                accountInfo.accumulate("id", id);
                accountInfo.accumulate("pw", pw);
                Log.i("DatabaseManager", "site : " + site);
                Log.i("DatabaseManager", "id : " + id);
                Log.i("DatabaseManager", "pw : " + pw);
                account.put(accountInfo);
            } catch (JSONException e) {
                throw new APIException("error while building JSON", APIException.SEARCH_FROM_PRIVATEINFO |
                        APIException.JSON_BUILD_ERR, e);
            }
        }
        try {
            json.accumulate("account", account);
        } catch (JSONException e) {
            throw new APIException("error while building JSON", APIException.SEARCH_FROM_PRIVATEINFO |
                    APIException.JSON_BUILD_ERR, e);
        }
        return json.toString();
    }
    void updateList(String source) throws APIException {
        myDatabase.execSQL("delete from " + TABLE_LIST);
        JSONObject json = null;
        try {
            json = new JSONObject(source);
        } catch (JSONException e) {
            throw new APIException("Could not create JSON",APIException.UPDATE_LIST |
                    APIException.JSON_SOURCE_ERR,e);
        }
        try {
            JSONArray list = null;
            list = json.getJSONArray("account");
            for (int i = 0; i < list.length(); i++) {
                JSONObject element = null;
                element = list.getJSONObject(i);
                String co_name = element.getString("subject");
                JSONObject validity = element.getJSONObject("validity");
                String notBefore = validity.getString("notBefore");
                String notAfter = validity.getString("notAfter");
                Log.i("DatabaseManager","updatePrivateInformation account[" +i +"] subject:" + co_name);
                Log.i("DatabaseManager","updatePrivateInformation account[" +i +"] notBefore:" + notBefore);
                Log.i("DatabaseManager","updatePrivateInformation account[" +i +"] notAfter:" + notAfter);
                ContentValues values = new ContentValues();
                values.put("co_name",co_name);
                values.put("co_active_date",notBefore);
                values.put("co_exp_date",notAfter);
                myDatabase.insertWithOnConflict(TABLE_LIST,null,values,SQLiteDatabase.CONFLICT_REPLACE);
            }
        } catch (JSONException e) {
            throw new APIException("Error pasring JSONArray [account]",APIException.UPDATE_LIST |
                    APIException.JSON_GET_ERR,e);
        }

    }
    void updatePrivateInformation(String source,String co_name) throws APIException {
        JSONObject json = null;
        try {
            json = new JSONObject(source);
        } catch (JSONException e) {
            throw new APIException("Could not create JSON",APIException.UPDATE_PRIVATEINFO |
                    APIException.JSON_SOURCE_ERR,e);
        }
        /*myDatabase.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_INFO+
                "(" +
                "co_name VARCHAR(50) PRIMARY KEY," +
                "cert_pw VARCHAR(50)," +
                "co_cert_type INTEGER,"+
                "co_cert_der VARCHAR(2560)," +
                "co_cert_key VARCHAR(2560)," +
                "co_certification VARCHAR(4096)," +
                "co_count INTEGER" +
                ");");*/
        try {
            String cert_pw = json.getString("cert_pw");
            int cert_type = json.getInt("cert_type");
            String co_cert_der = json.getString()
            JSONArray account = (JSONArray) json.get("account");
            for(int i = 0 ; i <account.length();i++) {
                JSONObject acc = account.getJSONObject(i);
                ContentValues vals = new ContentValues();
                String site = acc.getString("site");
                String id = acc.getString("id");
                String pw = acc.getString("pw");
                Log.i("DatabaseManager","updatePrivateInformation account[" +i +"] site:" + site);
                Log.i("DatabaseManager","updatePrivateInformation account[" +i +"] id:" + id);
                Log.i("DatabaseManager","updatePrivateInformation account[" +i +"] pw:" + pw);
                vals.put("co_name",co_name);
                vals.put("site",site);
                vals.put("id",id);
                vals.put("pw",pw);
                myDatabase.insertWithOnConflict(TABLE_SITE,null,vals,
                        SQLiteDatabase.CONFLICT_REPLACE);
            }
        } catch (JSONException e) {
            throw new APIException("ERR while getting Account Information",APIException.UPDATE_PRIVATEINFO |
                    APIException.JSON_GET_ERR,e);
        } catch (SQLException e) {
            throw new APIException("Error while inserting PrivateInformation",APIException.UPDATE_PRIVATEINFO |
                    APIException.SQL_INSERT_ERR,e);
        }
    }
}
