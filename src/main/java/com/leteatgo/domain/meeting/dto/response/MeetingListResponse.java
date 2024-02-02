package com.leteatgo.domain.meeting.dto.response;

import com.leteatgo.domain.meeting.dto.response.MeetingDetailResponse.RestaurantResponse;
import com.leteatgo.domain.meeting.type.MeetingStatus;
import java.time.LocalDateTime;

public record MeetingListResponse(
        Long meetingId,
        String meetingName,
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