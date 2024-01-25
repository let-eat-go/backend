package com.leteatgo.domain.auth.dto.request;

import static com.leteatgo.global.util.DtoValidator.EMAIL_MESSAGE;
import static com.leteatgo.global.util.DtoValidator.EMPTY_MESSAGE;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record EmailCheckRequest(
        @NotBlank(message = EMPTY_MESSAGE)
        @Email(message = EMAIL_MESSAGE)
        String email
) {

}
