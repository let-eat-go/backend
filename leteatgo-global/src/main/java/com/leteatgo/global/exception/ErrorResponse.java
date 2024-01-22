package com.leteatgo.global.exception;

import org.springframework.http.ResponseEntity;

public record ErrorResponse(
        ErrorCode code,
        String message
) {

    public static ResponseEntity<ErrorResponse> of(ErrorCode errorCode, String message) {
        return ResponseEntity.status(errorCode.getHttpStatus().value())
                .body(new ErrorResponse(errorCode, message));
    }
}
