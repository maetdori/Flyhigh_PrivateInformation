package com.private_information;

import androidx.annotation.NonNull;

public class APIException extends Exception {
    static public final int POSTSSL = 0x80000100;
    static public final int NO_INTERNET = 0x00000001;




    //DatabaseManager
    //Methods
    static public final int UPDATEPRIVATEINFO = 0x80010100;

    //ERRORS
    static public final int JSON_SOURCE_ERR = 0x01;

    static public final int AES_UTIL =0x80020000;



    private int code;
    public APIException(String message,int code) {
        super(message);
        this.code = code;
    }
    public APIException(String message,int code, Throwable e) {
        super(message,e);
        this.code = code;
    }
    @NonNull
    @Override
    public String toString() {
        return "error: " + getMessage() + " code:" + code;
    }
    public int getCode() {
        return code;
    }
}
