package com.leteatgo.domain.meeting.dto.response;

import com.leteatgo.domain.meeting.dto.response.MeetingDetailResponse.RestaurantResponse;
import com.leteatgo.domain.meeting.type.MeetingStatus;
import com.leteatgo.global.type.RestaurantCategory;
import java.time.LocalDateTime;

public record MeetingListResponse(
        Long meetingId,
        String meetingName,
        String region,
        RestaurantCategory category,
        Integer minParticipants,
        Integer maxParticipants,
        Integer currentParticipants,
        LocalDateTime startDateTime,
        LocalDateTime createdAt,
        String description,
        MeetingStatus status,
        RestaurantResponse restaurant
) {

}