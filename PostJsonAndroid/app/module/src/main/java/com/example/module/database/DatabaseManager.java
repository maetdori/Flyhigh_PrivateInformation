package com.example.module.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class DatabaseManager {
    static final String DB_NAME = "PrivateInfromantion";
    static final String TABLE_LIST = "LIST";
    static final String TABLE_INFO = "INFO";
    private Context myContext;
    private static DatabaseManager myDBManager = null;
    private SQLiteDatabase myDatabase = null;
    public static DatabaseManager getInstance(Context context) {
        if(myDBManager == null) {
            myDBManager = new DatabaseManager(context);
        }
        return myDBManager;
    }
    private DatabaseManager(Context context)
    {
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
                "version INTEGER," +
                "serialNumber VARCHAR(50)," +
                "signatureAlgorithm VARCHAR(50)," +
                "signatureHashAlgorithm VARCHAR(50)," +
                "issuer VARCHAR(50)," +
                "notBefore VARCHAR(8)," +
                "notAfter VARCHAR(8)," +
                "subject VARCHAR(50)," +
                "subjectPublicKeyAlgorithm VARCHAR(50)," +
                "subjectPublicKey VARCHAR(500)," +
                "issuerUniqueIdentifier VARCHAR(500)," +
                "subjectUniqueIdentifier VARCHAR(500)," +
                "certificateSignature VARCHAR(500)" +
                ");");
    }
}
