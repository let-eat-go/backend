package com.leteatgo.domain.auth.dto.request;

import static com.leteatgo.global.constants.DtoValid.EMAIL_MESSAGE;
import static com.leteatgo.global.constants.DtoValid.EMPTY_MESSAGE;
import static com.leteatgo.global.constants.DtoValid.PW_FORMAT;
import static com.leteatgo.global.constants.DtoValid.PW_MESSAGE;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record SignInRequest(
        @NotBlank(message = EMPTY_MESSAGE)
        @Email(message = EMAIL_MESSAGE)
        String email,
        @NotBlank(message = EMPTY_MESSAGE)
        @Pattern(regexp = PW_FORMAT, message = PW_MESSAGE)
        String password
) {

}
