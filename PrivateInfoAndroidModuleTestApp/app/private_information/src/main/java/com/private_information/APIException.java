package com.private_information;

import androidx.annotation.NonNull;

public class APIException extends Exception {
    //API
    static public final int API = 0x80000000;

    //methods
    static public final int POSTSSL = 0x80000100;
    static public final int CONV_IS_TO_STR = 0x80000200;
    static public final int CERT = 0x80000300;
    static public final int SET_CONNECTION = 0x80000400;
    static public final int REQ_CERT_B64 = 0x80000500;
    static public final int GET_CERT = 0x80000600;
    static public final int SET_HEADER = 0x80000700;






    //DatabaseManager
    static public final int DATABASEMANAGER = 0x80010000;
    //Methods
    static public final int GET_LIST = 0x80010100;
    static public final int UPDATE_LIST = 0x80010200;
    static public final int UPDATE_PRIVATEINFO = 0x80010300;
    static public final int SEARCH_FROM_PRIVATEINFO = 0x80010400;




    static public final int AES256UTIL =0x80020000;


    static public final int JSON_DECRYPT = 0x80030000;
    //methods
    static public final int DFS = 0x80030100;



    static public final int RSA_MODULE = 0x80040000;
    //methods
    static public final int RSA_ENCRYPT = 0x80040100;
    static public final int RSA_DECRYPT = 0x80040200;




    //ERRORS
    static public final int IOEXCEPTION = 0x01; // 파일입출력 중 발생하는 IOEXCEPTION
    static public final int INV_CERT_FORMAT = 0x02; // 인증서의 형식이 잘못됨
    static public final int CREATE_KS_ERROR = 0x03; // 주어진 입력으로 keystore를 생성할 수 없음
    static public final int CREATE_TMF_ERROR = 0x04;// 주어진 keystore로 trustManager를 생성 할 수 없음
    static public final int CREATE_SSL_ERROR = 0x05;// 주어진 trustManager로 SSLContext를 생성 할 수 없음
    static public final int INV_SERVERURL = 0x06;// serverURL이 잘못됨
    static public final int NULL_INPUTSTREAM = 0x07;// inputstream이 null임
    static public final int INV_ARGS = 0x08;// (자료형)...으로 들어온 파라미터가 형식에 맞지 않음
    static public final int JSON_SOURCE_ERR = 0x09;// 주어진 입력으로 JSON객체를 만들 수없음
    static public final int NO_SUCH_NAME = 0x0a;// DB에 주어진 조건에 맞는 row가 없음
    static public final int REQUIRED_VALUE_NULL = 0x0b; // not null이어야하는 value가 null로 들어옴
    static public final int JSON_BUILD_ERR = 0x0c; // JSON오브젝트에 요소 추가중 오류 발생
    static public final int JSON_GET_ERR = 0x0d; // JSON오브젝트의 요소가 없거나 접근 할 수 없음
    static public final int SQL_INSERT_ERR = 0x0e;// DB에 insert중 오류 발생
    static public final int DB_NO_DATA = 0x0f;// db에 데이터가 없음
    static public final int NO_INTERNET = 0x10;// 네트워크에 연결되지 않음
    static public final int RSA_NO_INSTANCE = 0x11;// RSA instance생성불가
    static public final int RSA_INV_KEY = 0x12;//RSA의 키가 잘못됨
    static public final int SSL_PEER_UNVERIFIED = 0x13;//서버의 SSL인증서를 받아올 때 발생가능, SSL통신하는 상대방이 식별되지않음
    static public final int SERVER_NOT_RESPONDING = 0x14; // connect에 대해 서버가 응답이 없거나 오류 발생
    static public final int INV_METHOD_URL = 0x15; // getList,getInfo,test이외의 메소드가 들어온경우






    private int code;
    public APIException(String message,int code) {
        super( message );
        this.code = code;
    }
    public APIException(String message,int code, Throwable e) {
        super(message,e);
        this.code = code;
    }
    @NonNull
    @Override
    public String toString() {
        return "error: " + getMessage() + " code:" + String.format("0x%x",code);
    }

    public int getCode() {
        return code;
    }
}
