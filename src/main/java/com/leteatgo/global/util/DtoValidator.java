package com.leteatgo.global.util;

public class DtoValidator {

    public static final String EMPTY_MESSAGE = "비어있는 항목을 입력해주세요.";
    public static final String EMAIL_MESSAGE = "올바른 이메일 형식이 아닙니다.";
    public static final String PW_MESSAGE = "비밀번호는 특수문자와 숫자를 포함하여 8자 이상 20자 이내로 작성 가능합니다.";
    public static final String NICKNAME_MESSAGE = "이름은 특수문자를 제외하고 공백없이 10자 이내로 작성 가능합니다.";
    public static final String PHONE_NUMBER_MESSAGE = "올바른 핸드폰 번호 형식이 아닙니다.";
    public static final String AUTH_CODE_MESSAGE = "인증번호는 6자리 숫자입니다.";

    public static final String PW_FORMAT = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W).{8,20}$";
    public static final String NICKNAME_FORMAT = "^[a-zA-Z0-9\\p{IsHangul}]{1,10}$";
    public static final String PHONE_NUMBER_FORMAT = "^01(?:0|1|[6-9])(?:\\d{3}|\\d{4})\\d{4}$";
    public static final String AUTH_CODE_FORMAT = "[0-9]{6}";

    public static final String EMPTY_KEYWORD = "검색어를 입력해주세요.";

}
