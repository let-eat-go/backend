package com.leteatgo.domain.auth.dto.request;

import static com.leteatgo.domain.member.type.MemberRole.ROLE_USER;
import static com.leteatgo.global.util.DtoValidator.EMAIL_MESSAGE;
import static com.leteatgo.global.util.DtoValidator.EMPTY_MESSAGE;
import static com.leteatgo.global.util.DtoValidator.NICKNAME_FORMAT;
import static com.leteatgo.global.util.DtoValidator.NICKNAME_MESSAGE;
import static com.leteatgo.global.util.DtoValidator.PHONE_NUMBER_FORMAT;
import static com.leteatgo.global.util.DtoValidator.PHONE_NUMBER_MESSAGE;
import static com.leteatgo.global.util.DtoValidator.PW_FORMAT;
import static com.leteatgo.global.util.DtoValidator.PW_MESSAGE;

import com.leteatgo.domain.member.entity.Member;
import com.leteatgo.domain.member.type.LoginType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record SignUpRequest(

        @NotBlank(message = EMPTY_MESSAGE)
        @Email(message = EMAIL_MESSAGE)
        String email,
        @NotBlank(message = EMPTY_MESSAGE)
        @Pattern(regexp = NICKNAME_FORMAT, message = NICKNAME_MESSAGE)
        String nickname,
        @NotBlank(message = EMPTY_MESSAGE)
        @Pattern(regexp = PW_FORMAT, message = PW_MESSAGE)
        String password,
        @NotBlank(message = EMPTY_MESSAGE)
        @Pattern(regexp = PW_FORMAT, message = PW_MESSAGE)
        String passwordCheck,
        @NotBlank(message = EMPTY_MESSAGE)
        @Pattern(regexp = PHONE_NUMBER_FORMAT, message = PHONE_NUMBER_MESSAGE)
        String phoneNumber
) {

    public static Member toEntity(SignUpRequest request, String password, LoginType loginType) {
        return Member.builder()
                .email(request.email())
                .nickname(request.nickname())
                .password(password)
                .phoneNumber(request.phoneNumber())
                .loginType(loginType)
                .role(ROLE_USER)
                .build();
    }
}
