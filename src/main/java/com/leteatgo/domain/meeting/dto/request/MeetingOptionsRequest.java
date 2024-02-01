package com.leteatgo.domain.meeting.dto.request;

import static com.leteatgo.global.constants.DtoValid.EMPTY_MESSAGE;

import com.leteatgo.domain.meeting.entity.MeetingOptions;
import com.leteatgo.domain.meeting.type.AgePreference;
import com.leteatgo.domain.meeting.type.AlcoholPreference;
import com.leteatgo.domain.meeting.type.GenderPreference;
import com.leteatgo.domain.meeting.type.MeetingPurpose;
import jakarta.validation.constraints.NotNull;

public record MeetingOptionsRequest(
        @NotNull(message = EMPTY_MESSAGE)
        GenderPreference genderPreference,
        @NotNull(message = EMPTY_MESSAGE)
        AgePreference agePreference,
        @NotNull(message = EMPTY_MESSAGE)
        MeetingPurpose purpose,
        @NotNull(message = EMPTY_MESSAGE)
        AlcoholPreference alcoholPreference
) {

    public static MeetingOptions toEntiy(MeetingOptionsRequest request) {
        return MeetingOptions.builder()
                .genderPreference(request.genderPreference())
                .agePreference(request.agePreference())
                .alcoholPreference(request.alcoholPreference())
                .purpose(request.purpose())
                .build();
    }
}