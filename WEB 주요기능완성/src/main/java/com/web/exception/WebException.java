package com.web.exception;

public class WebException extends Exception{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final int EXAMPLE_CODE = 0x80000000;
    /*코드형식 : 1~2 : (0x80 : 에러)
           * 3~4 : 클래스 코드
           * 5~6 : 메소드 코드
           * 7~8 : 일어난 문제
           * ex) code : 0x80010203
           *  01번 클래스의 02번 메소드에서 03에러발생
     */
	
	public static final int HOMECONTROLLER = 0x80010000;
	
	public static final int HC_HOME = 0x80010100;
	
	public static final int HC_REGISTER = 0x80010200;
	
	public static final int HC_MODIFY = 0x80010300;
	public static final int HC_MODIFY_NO_SUCH_NAME = 0x80010301; // name에 대응하는 인증서가 없음
	public static final int HC_MODIFY_DATABASE_ERROR = 0x80010302; // 데이터 베이스 접근중에 예외 발생
	
	
	
	
	
	public static final int WEBCONTROLLER = 0x80020000;
	
	public static final int WC_REGISTER = 0x80020100;
	
	public static final int WC_MODIFY = 0x80020200;
	
	public static final int WC_DELETE = 0x80020300;
	public static final int WC_DELETE_INV_NAME = 80020301; // request의 name을 찾지못함 (bad request)
	public static final int WC_DELETE_DATABASE_ERROR = 0x80020302; // 데이터 베이스 접근중에 예외 발생
	
	public static final int INSERT_OR_MODIFY = 0x80020400;
	public static final int WC_IOM_NO_CERT = 0x80020401; // request의 der, pfx을 찾지못함 (bad request)
	public static final int WC_IOM_NO_KEY = 0x80020402; // request의 key을 찾지못함 (bad request)
	public static final int WC_IOM_INV_DER = 0x80020403; // request의 der형식이 잘못됨 (bad request)
	public static final int WC_IOM_INV_PFX = 0x80020404; // request의 pfx형식이 잘못됨 (bad request)
	public static final int WC_IOM_PFX_DEC_ERR = 0x80020405; // request의 pw로 pfx를 복호화할 수없음 (bad request)
	public static final int WC_IOM_INV_NAME_PW = 0x80020406; // request의 name을 찾지못함 (bad request)
	public static final int WC_IOM_INV_SITE_INFO = 0x80020407; // request의 site_info의 형식이 잘못됨 (bad request)
	public static final int WC_IOM_DATABASE_ERROR = 0x80020408; // 데이터 베이스 접근중에 예외 발생
	
	
	
	
	
	
	public static final int WASCONTROLLER = 0x80030000;
	
	public static final int WSC_GETLIST = 0x80030100;
	
	public static final int WSC_GETINFO = 0x80030200;
	public static final int WSC_GETINFO_NO_SUCH_NAME = 0x80030201;// requset의 co_name에 대응되는 인증서를 찾을 수 없음
	
	
	
	
	
	public static final int RESPONSEENCRYPTMODULE = 0x80040000;
	
	public static final int RENCM_ENCRYPT = 0x80040100;
	public static final int RENCM_ENCRYPT_PUBKEY_INCORRECT = 0x80040101; // requset로 들어온 publickey와 서버인증서의 public key가 다름
	
	public static final int RENCM_LOADKEYSTORE = 0x80040200;
	public static final int RENCM_LOADKEYSTORE_NO_INSTANCE = 0x80040201; // PKCS12 instance를 가져올 수 없음
	public static final int RENCM_LOADKEYSTORE_FILENOTFOUND = 0x80040202; // 입력으로 주어진 파일이 없음
	public static final int RENCM_LOADKEYSTORE_LOAD = 0x80040203; // 모종의 이유로 keystore가 로드 되지않음(ex 패스워드 불일치)
	
	public static final int RENCM_GET_PRIVKEY_FROM_KS = 0x80040300;
	public static final int RENCM_GET_PRIVKEY_FROM_KS_KS_NO_INIT = 0x80040301; // keystore가 init되지 않음 (loadKeyStore함수의 결과값에 문제가 있는 경우)
	public static final int RENCM_GET_PRIVKEY_FROM_KS_WRONGPW = 0x80040302; // keystore패스워드 불일치 
	public static final int RENCM_GET_PRIVKEY_FROM_KS_UNKNOWNALG = 0x80040303; // keystore 암호화 알고리즘을 알 수 없음
	
	
	public static final int RENCM_LD_CERT_FROM_KS = 0x80040400;
	public static final int RENCM_LD_CERT_FROM_KS_KS_NO_INIT = 0x80040401; // keystore가 init되지 않음 (loadKeyStore함수의 결과값에 문제가 있는 경우)
	
	public static final int RENCM_DFS = 0x80040500;
	
	
	public static final int RENCM_GETKEY = 0x80040600;
	public static final int RENCM_GETKEY_CERT_ENCODING_ERROR = 0x80040601; // keystore에서 인증서를 인코딩할 수 없음
	
	
	
	
	
	
	
	public static final int AES256UTIL = 0x80050000;
	
	
	
	
	
	public static final int RSAMODULE = 0x80060000;
	
	public static final int RSA_ENC = 0x80060100;
	public static final int RSA_ENC_NO_INSTANCE = 0x80060101; // 자바에서 rsa암호화 모듈 instance을 가져올 수 없음
	public static final int RSA_ENC_INV_KEY = 0x80060102; // byte[] 로 들어온 키가 유효하지 않음
	
	public static final int RSA_DEC = 0x80060200;
	public static final int RSA_DEC_NO_INSTANCE = 0x80060201; // 자바에서 rsa암호화 모듈 instance을 가져올 수 없음
	public static final int RSA_DEC_INV_KEY = 0x80060202; // byte[] 로 들어온 키가 유효하지 않음

	

	
	
	
	
	
    private int code;
    
    
    public WebException(String message,int code) {
        super( message );
        this.code = code;
    }
    public WebException(String message,int code, Throwable cause) {
        super(message,cause);
        this.code = code;
    }
    @Override
    public String toString() {
        return "error: " + getMessage() + " code:" + String.format("0x%x",code);
    }
    
    public int getCode() {
        return code;
    }

}
