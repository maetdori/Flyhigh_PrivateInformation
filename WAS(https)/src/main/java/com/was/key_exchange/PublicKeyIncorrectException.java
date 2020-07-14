package com.was.key_exchange;

public class PublicKeyIncorrectException extends RuntimeException {
    private static final long serialVersionUID = -8460356990632230194L;
    public PublicKeyIncorrectException(String message, Throwable cause) {
        super(message, cause);
    }
    public PublicKeyIncorrectException(String message) {
        super(message);
    }
    public PublicKeyIncorrectException(Throwable cause) {
        super(cause);
    }
    public PublicKeyIncorrectException() {
        super();
    }
}