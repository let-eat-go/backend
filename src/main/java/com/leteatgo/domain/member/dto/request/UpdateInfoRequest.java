package com.leteatgo.domain.member.dto.request;

import static com.leteatgo.global.constants.DtoValid.EMPTY_MESSAGE;

import jakarta.validation.constraints.NotBlank;

public record UpdateInfoRequest(
        @NotBlank(message = EMPTY_MESSAGE)
        String nickname,

        @NotBlank(message = EMPTY_MESSAGE)
        String introduce
) {

}
