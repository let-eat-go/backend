package com.leteatgo.global.external.exception;

import com.leteatgo.global.exception.CustomException;
import com.leteatgo.global.exception.ErrorCode;

public class ApiBadRequestException extends CustomException {

    public ApiBadRequestException(ErrorCode errorCode) {
        super(errorCode);
    }

    public ApiBadRequestException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
