package com.leteatgo.domain.meeting.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.annotation.Nullable;
import java.time.LocalDate;
import java.time.LocalTime;

public record MeetingUpdateRequest(
        @JsonFormat(pattern = "yyyy-MM-dd")
        @Nullable
        LocalDate startDate,
        @JsonFormat(pattern = "HH:mm")
        @Nullable
        LocalTime startTime,
        @Nullable
        TastyRestaurantRequest restaurant
) {

}
