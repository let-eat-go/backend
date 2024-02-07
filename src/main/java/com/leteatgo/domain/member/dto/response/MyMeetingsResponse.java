package com.leteatgo.domain.member.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.leteatgo.global.type.RestaurantCategory;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record MyMeetingsResponse(
        Long meetingId,
        String meetingName,
        RestaurantCategory category,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime startDateTime,
        Integer maxParticipants,
        Restaurant restaurant
) {

    @Builder
    public record Restaurant(
            Long id,
            String name,
            String address,
            String phoneNumber
    ) {

    }
}
