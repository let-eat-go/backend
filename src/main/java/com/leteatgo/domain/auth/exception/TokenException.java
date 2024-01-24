package com.leteatgo.domain.auth.exception;

import com.leteatgo.global.exception.CustomException;
import com.leteatgo.global.exception.ErrorCode;

public class TokenException extends CustomException {

    public TokenException(ErrorCode errorCode) {
        super(errorCode);
    }

    public TokenException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
