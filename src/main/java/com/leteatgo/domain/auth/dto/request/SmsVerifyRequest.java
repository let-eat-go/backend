package com.leteatgo.domain.auth.dto.request;

import static com.leteatgo.global.constants.DtoValid.AUTH_CODE_FORMAT;
import static com.leteatgo.global.constants.DtoValid.AUTH_CODE_MESSAGE;
import static com.leteatgo.global.constants.DtoValid.EMPTY_MESSAGE;
import static com.leteatgo.global.constants.DtoValid.PHONE_NUMBER_FORMAT;
import static com.leteatgo.global.constants.DtoValid.PHONE_NUMBER_MESSAGE;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record SmsVerifyRequest(

        @NotBlank(message = EMPTY_MESSAGE)
        @Pattern(regexp = PHONE_NUMBER_FORMAT, message = PHONE_NUMBER_MESSAGE)
        String phoneNumber,
        @NotBlank(message = EMPTY_MESSAGE)
        @Pattern(regexp = AUTH_CODE_FORMAT, message = AUTH_CODE_MESSAGE)
        String authCode
) {

}
