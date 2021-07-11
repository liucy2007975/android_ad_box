package com.cow.liucy.libcommon.api.http.exception;

/**
 * 异常处理类，将异常包装成一个 ApiException ,抛给上层统一处理
 */

public class ApiException extends RuntimeException {
    private String errorCode;

    public ApiException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
