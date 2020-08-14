package com.private_information;

public class WASJSONConsts {
    /**
     * WAS related consts : 안드로이드 클라이언트와 주고받는 HttpRequset/Response관련 상수 안드로이드 모듈 쪽
     * 소스와 동기화 되어야한다.
     */
    // method path
    public static final String METHOD_PATH = "/private";
    // POST method name
    public static final String GET_LIST = "/getList";
    public static final String GET_INFO = "/getInfo";
    public static final String GET_CERT = "/getCert";

    // requset Header Key
    public static final String REQ_HDR_CONTENT_TYPE = "Content-type";
    public static final String REQ_HDR_DEVICE_ID = "Device-Id";
    public static final String REQ_HDR_SERVER_CERT_ID = "Server-Cert-Id";

    // ResponseHeader Key
    public static final String REQ_HDR_LOCATION = "Location";

    // ResponseHeader Value
    public static final String RES_HDR_CONTENT_TYPE_VAL = "application/json";
    public static final String RES_HDR_LOCATION_VAL = "https://ec2-13-124-68-42.ap-northeast-2.compute.amazonaws.com:443/";


    public static final String STRING_USERNAME = "username";
    // getList ResponseBody Key
    public static final String JO_ACCOUNT = "account";
    public static final String STRING_SUBJECT = "subject";
    public static final String JO_VALIDITY = "validity";
    public static final String STRING_NOT_BEFORE = "notBefore";
    public static final String STRING_NOT_AFTER = "notAfter";
    public static final String STRING_COUNT = "count";

    // getInfo ResponseBody Key
    public static final String STRING_DOMAIN = "site";
    public static final String STRING_ID = "id";
    public static final String STRING_PW = "pw";

    public static final String STRING_CERT_PW = "cert_pw";
    public static final String INTEGER_CERT_TYPE = "cert_type";
    public static final String JO_CERTIFICATION = "certification";
    public static final String STRING_DER = "der";
    public static final String STRING_KEY = "key";
    public static final String STRING_PFX = "pfx";

    public static final String JO_PERSONALINFO = "personalInfo";
    public static final String STRING_KNAME = "kname";
    public static final String STRING_ENAME = "ename";
    public static final String BOOLEAN_CORP = "corp";
    public static final String STRING_ADDR1 = "addr1";
    public static final String STRING_ADDR2 = "addr2";
    public static final String STRING_ADDR3 = "addr3";
    public static final String STRING_CAR = "car";
    public static final String STRING_HOJUK_NAME = "hojuk_name";
    public static final String STRING_HOUSE_HOLD = "house_hold";
    public static final String STRING_RELATION = "relation";
    public static final String STRING_RELATION_NAME = "relation_name";
    public static final String STRING_RRN1 = "rrn1";
    public static final String STRING_RRN2 = "rrn2";
    public static final String STRING_SAUPJA_NUM = "saupja_num";
    public static final String STRING_TEL = "tel";

    // getCert ResponseBody Key
    public static final String STRING_CERT_BASE64 = "cert_base64";

    // ResponseEncryptModule Key
    public static final String STRING_PUBKEY = "pubKey";
    public static final String STRING_CKEY = "cKey";
    public static final String STRING_SKEY = "sKey";
    public static final String JO_ENCRYPTED_ELEMENTS = "encryptedElements";
    public static final String STRING_ENCNAME = "name";

    // ErrorResponse Key
    public static final String JO_EXCEPTION = "Exception";
    public static final String STRING_ERROR_CODE = "error_code";
    public static final String STRING_ERROR_MESSAGE = "error_message";

}
