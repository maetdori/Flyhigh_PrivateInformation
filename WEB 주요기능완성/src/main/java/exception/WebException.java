package exception;

public class WebException extends Exception{
	public static final int EXAMPLE_CODE = 0x80000000;
    /*코드형식 : 1~2 : 0x80에러
           * 3~4 : 클래스 코드
           * 5~6 : 메소드 코드
           * 7~8 : 일어난 문제
           * ex) code : 0x08010203
           *  01번 클래스의 02번 메소드에서 03에러발생
     */
	
	public static final int HOMECONTROLLER = 0x80010000;
	
	public static final int HOME = 0x80010100;
	public static final int REGISTER = 0x80010200;
	
	public static final int MODIFY = 0x80010300;
	public static final int NO_SUCH_NAME = 0x01;
	public static final int DATABASE_ERROR = 0x02;
	
	
	
	
	
	public static final int WEBCONTROLLER = 0x80020000;
	
	public static final int INSERT_OR_MODIFY = 0x80020400;
	
	
	
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
