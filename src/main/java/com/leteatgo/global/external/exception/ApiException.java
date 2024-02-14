package com.leteatgo.global.external.exception;

import com.leteatgo.global.exception.CustomException;
import com.leteatgo.global.exception.ErrorCode;

public class ApiException extends CustomException {

    public ApiException(ErrorCode errorCode) {
        super(errorCode);
    }

    public ApiException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
