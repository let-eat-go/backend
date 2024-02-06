package com.leteatgo.global.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

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
    SECURITY_UNAUTHORIZED(UNAUTHORIZED, "인증에 실패하였습니다."),

    // auth
    ALREADY_EXIST_EMAIL(BAD_REQUEST, "이미 존재하는 이메일입니다."),
    WRONG_PASSWORD(BAD_REQUEST, "비밀번호가 일치하지 않습니다."),
    WRONG_AUTH_CODE(BAD_REQUEST, "인증번호가 일치하지 않습니다."),
    SMS_SEND_ERROR(INTERNAL_SERVER_ERROR, "문자 발송에 실패했습니다."),
    WRONG_PHONE_NUMBER(BAD_REQUEST, "올바르지 않은 핸드폰 번호입니다."),
    EXPIRED_AUTH_CODE(BAD_REQUEST, "만료된 인증번호입니다."),
    ALREADY_VERIFIED(BAD_REQUEST, "이미 인증된 핸드폰 번호입니다."),
    PHONE_NUMBER_NOT_VERIFIED(UNAUTHORIZED, "핸드폰 번호 인증이 완료되지 않았습니다."),
    ALREADY_EXIST_PHONE_NUMBER(BAD_REQUEST, "이미 존재하는 핸드폰 번호입니다."),
    ACCESS_DENIED(FORBIDDEN, "접근이 거부되었습니다."),
    ILLEGAL_PROVIDER(BAD_REQUEST, "지원하지 않는 OAuth2 공급자입니다."),

    // token
    INVALID_TOKEN(BAD_REQUEST, "올바르지 않은 토큰입니다."),
    EXPIRED_TOKEN(UNAUTHORIZED, "만료된 토큰입니다."),
    EMPTY_TOKEN(BAD_REQUEST, "토큰이 존재하지 않습니다."),

    // member
    NOT_FOUND_MEMBER(BAD_REQUEST, "존재하지 않는 회원입니다."),

    // tastyRestaurant
    NOT_FOUND_CATEGORY(NOT_FOUND, "존재하지 않는 카테고리입니다."),

    // chat
    NOT_FOUND_CHATROOM(NOT_FOUND, "존재하지 않는 채팅방입니다."),
    ALREADY_CLOSED_CHATROOM(BAD_REQUEST, "이미 종료된 채팅방입니다."),
    ILLEGAL_DESTINATION(BAD_REQUEST, "잘못된 구독 경로입니다."),

    // meeting
    NOT_FOUND_MEETING(BAD_REQUEST, "존재하지 않는 모임입니다."),
    NOT_MEETING_HOST(BAD_REQUEST, "모임의 주최자가 아닙니다."),
    CANNOT_CANCEL_MEETING(BAD_REQUEST, "모임 시작 1시간 전까지만 취소할 수 있습니다."),
    ALREADY_CANCELED_MEETING(BAD_REQUEST, "이미 취소된 모임입니다."),
    ALREADY_COMPLETED_MEETING(BAD_REQUEST, "이미 종료된 모임입니다."),
    NOT_FOUND_SEARCH_TYPE(BAD_REQUEST, "존재하지 않는 검색 타입입니다."),
    ALREADY_FULL_PARTICIPANT(BAD_REQUEST, "이미 참가인원이 가득 찬 모임입니다."),
    ALREADY_JOINED_MEETING(BAD_REQUEST, "이미 참가한 모임입니다."),
    NOT_JOINED_MEETING(BAD_REQUEST, "참가하지 않은 모임입니다."),
    HOST_CANNOT_LEAVE_MEETING(BAD_REQUEST, "주최자는 모임에서 나갈 수 없습니다."),

    // region
    NOT_FOUND_REGION(BAD_REQUEST, "존재하지 않는 지역입니다.");

    private final HttpStatus httpStatus;
    private final String errorMessage;
}
