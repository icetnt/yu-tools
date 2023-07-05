package com.yu.yutool.exception;

public class BaseException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    protected String code;

    public BaseException(String message) {
        super(message);
        this.code = "500";
    }

    public BaseException(String code, String message) {
        super(message);
        this.code = code;
    }

    public BaseException(String code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public BaseException(String msg, Throwable cause) {
        super(msg, cause);
        this.code = "500";
    }

    public String getCode() {
        return code;
    }
}
