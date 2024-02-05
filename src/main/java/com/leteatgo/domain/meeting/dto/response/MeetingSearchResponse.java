package com.leteatgo.domain.meeting.dto.response;

import com.leteatgo.domain.meeting.type.MeetingStatus;
import com.leteatgo.global.type.RestaurantCategory;
import java.time.LocalDateTime;

public record MeetingSearchResponse(
        Long id,
        String meetingName,
        String restaurantName,
        String restaurantAddress,
        RestaurantCategory category,
        LocalDateTime startDateTime,
        Integer minParticipants,
        Integer maxParticipants,
        Integer currentParticipants,
        MeetingStatus status
) {

}
