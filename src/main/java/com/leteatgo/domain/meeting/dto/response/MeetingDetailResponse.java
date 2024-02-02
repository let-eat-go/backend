package com.leteatgo.domain.meeting.dto.response;

import com.leteatgo.domain.meeting.type.MeetingStatus;
import java.time.LocalDateTime;
import java.util.List;

public record MeetingDetailResponse(
        MeetingResponse meeting,
        HostResponse host,
        List<ParticipantResponse> participants,
        RestaurantResponse restaurant,
        int chatRoomId

) {

    public record MeetingResponse(
            Long id,
            String name,
            Integer minParticipants,
            Integer maxParticipants,
            Integer currentParticipants,
            LocalDateTime startDateTime,
            String description,
            MeetingStatus status
    ) {

    }

    public record HostResponse(
            Long id,
            String nickname,
            String profileImageUrl
    ) {

    }

    public record ParticipantResponse(
            Long id,
            String nickname,
            String profileImageUrl
    ) {

    }

    public record RestaurantResponse(
            Long id,
            String name,
            String address,
            String phoneNumber
    ) {

    }
}
