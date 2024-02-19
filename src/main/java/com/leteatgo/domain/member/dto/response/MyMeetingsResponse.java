package com.leteatgo.domain.member.dto.response;

import com.leteatgo.global.type.RestaurantCategory;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record MyMeetingsResponse(
        Long meetingId,
        String meetingName,
        String region,
        RestaurantCategory category,
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
