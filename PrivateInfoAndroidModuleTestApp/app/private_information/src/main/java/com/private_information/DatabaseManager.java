package com.private_information;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Base64;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


class DatabaseManager {
    //consts
    static final String DB_NAME = "PrivateInfromantion.db";
    static final String KEY = "han1g";
    static final String TABLE_LIST = "LIST";
    static final String TABLE_INFO = "INFO";
    static final String TABLE_SITE = "SITE";

    //fields
    private Context myContext;
    private static DatabaseManager myDBManager = null;
    private SQLiteDatabase myDatabase = null;
    private byte[] aesKey;
    private AES256Util aes;
    //constructor
    private DatabaseManager(Context context) {
        aesKey = keyGen(KEY);
        aes = new AES256Util(aesKey);
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
                "co_cert_pw VARCHAR(88)," +
                "co_cert_type INTEGER,"+
                "co_cert_der VARCHAR(4096)," +
                "co_cert_key VARCHAR(4096)," +
                "co_certification VARCHAR(8192) ," +
                "co_kname VARCHAR(50)," +
                "co_ename VARCHAR(50), " +
                "co_corp INTEGER , " +
                "co_rrn1 VARCHAR(44) , " +
                "co_rrn2 VARCHAR(44) , " +
                "co_tel VARCHAR(44), " +
                "co_addr1 VARCHAR(100) ," +
                "co_addr2 VARCHAR(100) ," +
                "co_addr3 VARCHAR(100) ,"+
                "co_relation VARCHAR(6) ,"+
                "co_relation_name VARCHAR(50) ,"+
                "co_house_hold VARCHAR(50) ,"+
                "co_hojuk_name VARCHAR(50) ,"+
                "co_car VARCHAR(20) ,"+
                "co_saupja_num VARCHAR(44)"+
                ");");

        myDatabase.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_SITE+
                "(" +
                "co_name VARCHAR(50)," +
                "site VARCHAR(50)," +
                "id VARCHAR(64)," +
                "pw VARCHAR(88)" +
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

    private static byte[] keyGen(String key) {
        try{
            MessageDigest sh = MessageDigest.getInstance("SHA-256");
            sh.update(key.getBytes());
            byte byteData[] = sh.digest();
            return byteData;
        }catch(NoSuchAlgorithmException e) { e.printStackTrace();}
        return null;
    }

    String getList() throws APIException {
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
            /*"co_name VARCHAR(50) PRIMARY KEY," +
                    "co_cert_pw VARCHAR(88)," +
                    "co_cert_type INTEGER,"+
                    "co_cert_der VARCHAR(4096)," +
                    "co_cert_key VARCHAR(4096)," +
                    "co_certification VARCHAR(8192) ," +
                    "co_kname VARCHAR(50)," +
                    "co_ename VARCHAR(50), " +
                    "co_corp INTEGER , " +
                    "co_rrn1 VARCHAR(44) , " +
                    "co_rrn2 VARCHAR(44) , " +
                    "co_tel VARCHAR(44), " +
                    "co_addr1 VARCHAR(100) ," +
                    "co_addr2 VARCHAR(100) ," +
                    "co_addr3 VARCHAR(100) ,"+
                    "co_relation VARCHAR(6) ,"+
                    "co_relation_name VARCHAR(50) ,"+
                    "co_house_hold VARCHAR(50) ,"+
                    "co_hojuk_name VARCHAR(50) ,"+
                    "co_car VARCHAR(20) ,"+
                    "co_saupja_num VARCHAR(44)"+*/
            String cert_pw = new String(aes.decrypt(Base64.decode(cur.getString(1),Base64.NO_WRAP)));
            int cert_type = cur.getInt(2);
            String cert_der = new String(aes.decrypt(Base64.decode(cur.getString(3),Base64.NO_WRAP)));
            String cert_key = new String(aes.decrypt(Base64.decode(cur.getString(4),Base64.NO_WRAP)));
            String cert_pfx = new String(aes.decrypt(Base64.decode(cur.getString(5),Base64.NO_WRAP)));
            String kname = cur.getString(6);
            String ename = cur.getString(7);
            boolean corp = cur.getInt(8) != 0;
            String rrn1 = new String(aes.decrypt(Base64.decode(cur.getString(9),Base64.NO_WRAP)));
            String rrn2 = new String(aes.decrypt(Base64.decode(cur.getString(10),Base64.NO_WRAP)));
            String tel = new String(aes.decrypt(Base64.decode(cur.getString(11),Base64.NO_WRAP)));
            String addr1 = cur.getString(12);
            String addr2 = cur.getString(13);
            String addr3 = cur.getString(14);
            String relation = cur.getString(15);
            String relation_name = cur.getString(16);
            String house_hold = cur.getString(17);
            String hojuk_name = cur.getString(18);
            String car = cur.getString(19);
            String saupja_num = new String(aes.decrypt(Base64.decode(cur.getString(20),Base64.NO_WRAP)));


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
            JSONObject personalInfo = new JSONObject();
            personalInfo.accumulate("ename",ename);
            personalInfo.accumulate("kname",kname);
            personalInfo.accumulate("corp",corp);
            personalInfo.accumulate("addr1",addr1);
            personalInfo.accumulate("addr2",addr2);
            personalInfo.accumulate("addr3",addr3);
            personalInfo.accumulate("car",car);
            personalInfo.accumulate("hojuk_name",hojuk_name);
            personalInfo.accumulate("house_hold",house_hold);
            personalInfo.accumulate("relation",relation);
            personalInfo.accumulate("relation_name",relation_name);
            personalInfo.accumulate("rrn1",rrn1);
            personalInfo.accumulate("rrn2",rrn2);
            personalInfo.accumulate("saupja_num",saupja_num);
            personalInfo.accumulate("tel",tel);
            json.accumulate("personalInfo",personalInfo);




           // privateInfo.acc

            Log.i("DatabaseManager", "cert_pw : " + cert_pw);
            Log.i("DatabaseManager", "cert_type : " + cert_type);
            Log.i("DatabaseManager", "cert_der : " + cert_der);
            Log.i("DatabaseManager", "cert_key : " + cert_key);
            Log.i("DatabaseManager", "cert_pfx : " + cert_pfx);
        } catch (JSONException e) {
            throw new APIException("error while building JSON", APIException.SEARCH_FROM_PRIVATEINFO |
                    APIException.JSON_BUILD_ERR, e);
        }
        try {
            cur = myDatabase.rawQuery("SELECT * FROM " + TABLE_SITE + " WHERE co_name = " + name + ";"
                    , null);
        } catch(SQLException e) {
            cur = null;
        }
        JSONArray account = new JSONArray();
        while (cur != null && cur.moveToNext()) {
            JSONObject accountInfo = new JSONObject();
            try {
                String site = cur.getString(1);
                String id = new String(aes.decrypt(Base64.decode(cur.getString(2),Base64.NO_WRAP)));
                String pw = new String(aes.decrypt(Base64.decode(cur.getString(3),Base64.NO_WRAP)));
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
        /*"co_name VARCHAR(50) PRIMARY KEY," +
                "co_cert_pw VARCHAR(50)," +
                "co_cert_type INTEGER,"+
                "co_cert_der VARCHAR(4096)," +
                "co_cert_key VARCHAR(4096)," +
                "co_certification VARCHAR(8192) ," +
                "co_kname VARCHAR(50)," +
                "co_ename VARCHAR(50), " +
                "co_corp INTEGER , " +
                "co_rrn1 VARCHAR(44) , " +
                "co_rrn2 VARCHAR(44) , " +
                "co_tel VARCHAR(44), " +
                "co_addr1 VARCHAR(100) ," +
                "co_addr2 VARCHAR(100) ," +
                "co_addr3 VARCHAR(100) ,"+
                "co_relation VARCHAR(6) ,"+
                "co_relation_name VARCHAR(50) ,"+
                "co_house_hold VARCHAR(50) ,"+
                "co_hojuk_name VARCHAR(50) ,"+
                "co_car VARCHAR(20) ,"+
                "co_saupja_num VARCHAR(44)"+
                ");");*/
        try {
            String cert_pw = Base64.encodeToString(
                    aes.encrypt(json.getString("cert_pw").getBytes()),Base64.NO_WRAP);
            int cert_type = json.getInt("cert_type"); // 암호화해서 저장
            String co_cert_der = Base64.encodeToString(
                    aes.encrypt(json.getJSONObject("certification").getString("der").getBytes())
                    ,Base64.NO_WRAP);
            String co_cert_key = Base64.encodeToString(
                    aes.encrypt(json.getJSONObject("certification").getString("key").getBytes())
                    ,Base64.NO_WRAP);
            String co_certification = Base64.encodeToString(
                    aes.encrypt(json.getJSONObject("certification").getString("pfx").getBytes())
                    ,Base64.NO_WRAP);
            String ename = json.getJSONObject("personalInfo").getString("ename");
            String kname = json.getJSONObject("personalInfo").getString("kname");
            boolean corp = json.getJSONObject("personalInfo").getBoolean("corp");
            String rrn1 = Base64.encodeToString(
                    aes.encrypt(json.getJSONObject("personalInfo").getString("rrn1").getBytes())
                    ,Base64.NO_WRAP); // 암호화해서 저장
            String rrn2 = Base64.encodeToString(
                    aes.encrypt(json.getJSONObject("personalInfo").getString("rrn2").getBytes())
                    ,Base64.NO_WRAP); //암호화해서 저장
            String tel = Base64.encodeToString(
                    aes.encrypt(json.getJSONObject("personalInfo").getString("tel").getBytes()),
                    Base64.NO_WRAP); //암호화 해서 저장
            String addr1 = json.getJSONObject("personalInfo").getString("addr1");
            String addr2 = json.getJSONObject("personalInfo").getString("addr2");
            String addr3 = json.getJSONObject("personalInfo").getString("addr3");
            String relation = json.getJSONObject("personalInfo").getString("relation");
            String relation_name = json.getJSONObject("personalInfo").getString("relation_name");
            String house_hold = json.getJSONObject("personalInfo").getString("house_hold");
            String hojuk_name = json.getJSONObject("personalInfo").getString("hojuk_name");
            String car = json.getJSONObject("personalInfo").getString("car");

            String saupja_num = Base64.encodeToString(
                    aes.encrypt(json.getJSONObject("personalInfo").getString("saupja_num").getBytes()),
                    Base64.NO_WRAP); //암호화해서 저장


            ContentValues vals = new ContentValues();

            vals.put("co_name",co_name);
            vals.put("co_cert_der",co_cert_der);
            vals.put("co_cert_key",co_cert_key);
            vals.put("co_certification",co_certification);
            vals.put("co_cert_type",cert_type);
            vals.put("co_cert_pw",cert_pw);
            vals.put("co_ename",ename);
            vals.put("co_kname",kname);
            vals.put("co_corp",corp);
            vals.put("co_rrn1",rrn1);
            vals.put("co_rrn2",rrn2);
            vals.put("co_tel",tel);
            vals.put("co_addr1",addr1);
            vals.put("co_addr2",addr2);
            vals.put("co_addr3",addr3);
            vals.put("co_relation",relation);
            vals.put("co_relation_name",relation_name);
            vals.put("co_house_hold",house_hold);
            vals.put("co_hojuk_name",hojuk_name);
            vals.put("co_car",car);
            vals.put("co_saupja_num",saupja_num);

            myDatabase.insertWithOnConflict(TABLE_INFO,null,vals,
                    SQLiteDatabase.CONFLICT_REPLACE);

            JSONArray account = (JSONArray) json.get("account");
            for(int i = 0 ; i <account.length();i++) {
                JSONObject acc = account.getJSONObject(i);
                vals = new ContentValues();
                String site = acc.getString("site");
                String id = Base64.encodeToString(
                        aes.encrypt(acc.getString("id").getBytes()),Base64.NO_WRAP);
                String pw = Base64.encodeToString(
                        aes.encrypt(acc.getString("pw").getBytes()),Base64.NO_WRAP);
                Log.d("DatabaseManager","updatePrivateInformation account[" +i +"] site:" + site);
                Log.d("DatabaseManager","updatePrivateInformation account[" +i +"] id:" + id);
                Log.d("DatabaseManager","updatePrivateInformation account[" +i +"] pw:" + pw);
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
    String getNpki(String name) throws APIException{
        Cursor cur = myDatabase.rawQuery("SELECT co_cert_der,co_cert_key FROM " + TABLE_INFO + " WHERE co_name = '"
                + name + "';", null);
        JSONObject json = new JSONObject();
        if (!cur.moveToNext()) {
            throw new APIException("No such name in local Database", APIException.SEARCH_FROM_PRIVATEINFO |
                    APIException.NO_SUCH_NAME);
        }
        String cert_der = new String(aes.decrypt(Base64.decode(cur.getString(0),Base64.NO_WRAP)));
        String cert_key = new String(aes.decrypt(Base64.decode(cur.getString(1),Base64.NO_WRAP)));

        try {
            json.accumulate("der", cert_der);
            json.accumulate("key", cert_key);
        } catch (JSONException e) {
            throw new APIException("error while building JSON", APIException.SEARCH_FROM_PRIVATEINFO |
                    APIException.JSON_BUILD_ERR, e);
        }

        return json.toString();
    }
}
