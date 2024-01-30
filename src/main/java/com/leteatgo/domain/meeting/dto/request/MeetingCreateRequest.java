package com.leteatgo.domain.meeting.dto.request;

import static com.leteatgo.global.constants.DtoValid.CATEGORY_MESSAGE;
import static com.leteatgo.global.constants.DtoValid.EMPTY_MESSAGE;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.leteatgo.global.validator.annotation.ValidRestaurantCategory;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Builder;

@Builder
public record MeetingCreateRequest(
        @NotBlank(message = EMPTY_MESSAGE)
        String name,
        @ValidRestaurantCategory(message = CATEGORY_MESSAGE)
        String category,
        @NotBlank(message = EMPTY_MESSAGE)
        String region,
        @Min(2)
        int maxParticipants,
        @Min(2)
        int minParticipants,
        @NotNull(message = EMPTY_MESSAGE)
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate startDate,
        @NotNull(message = EMPTY_MESSAGE)
        @JsonFormat(pattern = "HH:mm")
        LocalTime startTime,
        @NotBlank(message = EMPTY_MESSAGE)
        String description,
        @Valid
        MeetingOptionsRequest options,
        @Nullable
        TastyRestaurantRequest restaurant
) {

}