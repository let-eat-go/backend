package com.leteatgo.domain.meeting.dto.request;

import static com.leteatgo.global.constants.DtoValid.EMPTY_MESSAGE;

import jakarta.validation.constraints.NotBlank;

public record MeetingCancelRequest(
        @NotBlank(message = EMPTY_MESSAGE)
        String reason
) {

}
