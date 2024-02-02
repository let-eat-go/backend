package com.leteatgo.domain.meeting.dto.response;

import com.leteatgo.domain.meeting.type.MeetingStatus;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

public record MeetingDetailResponse(
        Long meetingId,
        String meetingName,
        HostResponse host,
        RestaurantResponse restaurant,
        Long chatRoomId,
        List<ParticipantResponse> participants,
        int minParticipants,
        int maxParticipants,
        int currentParticipants,
        LocalDateTime startDateTime,
        String description,
        MeetingStatus status
) {

    @Builder
    public record HostResponse(
            Long id,
            String nickname,
            String profileImageUrl
    ) {

    }

    @Builder
    public record RestaurantResponse(
            Long id,
            String name,
            String address,
            String phoneNumber
    ) {

    }

    @Builder
    public record ParticipantResponse(
            Long id,
            String nickname,
            String profileImageUrl
    ) {

    }
}

