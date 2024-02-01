package com.leteatgo.domain.region.exception;

import com.leteatgo.global.exception.CustomException;
import com.leteatgo.global.exception.ErrorCode;

public class RegionException extends CustomException {

    public RegionException(ErrorCode errorCode) {
        super(errorCode);
    }

    public RegionException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
