package com.leteatgo.domain.auth.exception;

import com.leteatgo.global.exception.CustomException;
import com.leteatgo.global.exception.ErrorCode;

public class AuthException extends CustomException {

    public AuthException(ErrorCode errorCode) {
        super(errorCode);
    }

    public AuthException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
