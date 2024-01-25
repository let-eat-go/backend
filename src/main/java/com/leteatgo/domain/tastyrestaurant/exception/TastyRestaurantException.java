package com.leteatgo.domain.tastyrestaurant.exception;

import com.leteatgo.global.exception.CustomException;
import com.leteatgo.global.exception.ErrorCode;

public class TastyRestaurantException extends CustomException {
    public TastyRestaurantException(ErrorCode errorCode) {
        super(errorCode);
    }

    public TastyRestaurantException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
