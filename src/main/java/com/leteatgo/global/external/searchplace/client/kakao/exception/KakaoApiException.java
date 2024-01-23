package com.leteatgo.global.external.searchplace.client.kakao.exception;

import com.leteatgo.global.exception.CustomException;
import com.leteatgo.global.exception.ErrorCode;

public class KakaoApiException extends CustomException {

    public KakaoApiException(ErrorCode errorCode) {
        super(errorCode);
    }

    public KakaoApiException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
