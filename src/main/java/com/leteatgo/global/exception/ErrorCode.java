package com.leteatgo.global.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // global
    RESOURCE_NOT_FOUND(NOT_FOUND, "요청한 자원을 찾을 수 없습니다."),
    INVALID_REQUEST(BAD_REQUEST, "올바르지 않은 요청입니다."),
    INTERNAL_ERROR(INTERNAL_SERVER_ERROR, "예상치 못한 내부 에러가 발생했습니다."),

    // auth
    ALREADY_EXIST_EMAIL(BAD_REQUEST, "이미 존재하는 이메일입니다."),
    ALREADY_EXIST_NICKNAME(BAD_REQUEST, "이미 존재하는 닉네임입니다."),
    WRONG_PASSWORD(BAD_REQUEST, "비밀번호가 일치하지 않습니다."),
    WRONG_AUTH_CODE(BAD_REQUEST, "인증번호가 일치하지 않습니다."),
    SMS_SEND_ERROR(BAD_REQUEST, "문자 발송에 실패했습니다."),
    WRONG_PHONE_NUMBER(BAD_REQUEST, "올바르지 않은 핸드폰 번호입니다."),
    ALREADY_VERIFIED(BAD_REQUEST, "이미 인증된 핸드폰 번호입니다."),
    PHONE_NUMBER_NOT_VERIFIED(BAD_REQUEST, "핸드폰 번호 인증이 완료되지 않았습니다."),
    ALREADY_EXIST_PHONE_NUMBER(BAD_REQUEST, "이미 존재하는 핸드폰 번호입니다.");

    private final HttpStatus httpStatus;
    private final String errorMessage;
}
