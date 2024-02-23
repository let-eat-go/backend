package com.leteatgo.domain.meeting.dto.response;

import com.leteatgo.domain.meeting.type.AgePreference;
import com.leteatgo.domain.meeting.type.AlcoholPreference;
import com.leteatgo.domain.meeting.type.GenderPreference;
import com.leteatgo.domain.meeting.type.MeetingPurpose;
import com.leteatgo.domain.meeting.type.MeetingStatus;
import com.leteatgo.global.type.RestaurantCategory;
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
            String region,
            RestaurantCategory category,
            Integer minParticipants,
            Integer maxParticipants,
            Integer currentParticipants,
            LocalDateTime startDateTime,
            String description,
            String cancelReason,
            MeetingStatus status,
            GenderPreference genderPreference,
            AgePreference agePreference,
            MeetingPurpose purpose,
            AlcoholPreference alcoholPreference
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
            Long apiId,
            String name,
            String roadAddress,
            String landAddress,
            String phoneNumber,
            Double latitude,
            Double longitude,
            String restaurantUrl
    ) {

    }
}
