package com.leteatgo.global.storage.exception;

import com.leteatgo.global.exception.CustomException;
import com.leteatgo.global.exception.ErrorCode;

public class StorageException extends CustomException {

    public StorageException(ErrorCode errorCode) {
        super(errorCode);
    }

    public StorageException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
