package com.leteatgo.domain.auth.dto.request;

import static com.leteatgo.global.util.DtoValidator.EMPTY_MESSAGE;
import static com.leteatgo.global.util.DtoValidator.PHONE_NUMBER_FORMAT;
import static com.leteatgo.global.util.DtoValidator.PHONE_NUMBER_MESSAGE;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record SmsSendRequest(

        @NotBlank(message = EMPTY_MESSAGE)
        @Pattern(regexp = PHONE_NUMBER_FORMAT, message = PHONE_NUMBER_MESSAGE)
        String phoneNumber
) {

}
