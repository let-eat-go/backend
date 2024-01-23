package com.leteatgo.domain.chat.exception;

import com.leteatgo.global.exception.CustomException;
import com.leteatgo.global.exception.ErrorCode;

public class ChatException extends CustomException {
    public ChatException(ErrorCode errorCode) {
        super(errorCode);
    }

    public ChatException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
