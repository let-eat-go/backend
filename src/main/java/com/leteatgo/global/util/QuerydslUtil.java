package com.leteatgo.global.util;

import com.leteatgo.domain.meeting.dto.response.MeetingDetailResponse;
import com.leteatgo.domain.meeting.dto.response.MeetingDetailResponse.HostResponse;
import com.leteatgo.domain.meeting.dto.response.MeetingDetailResponse.MeetingResponse;
import com.leteatgo.domain.meeting.dto.response.MeetingDetailResponse.ParticipantResponse;
import com.leteatgo.domain.meeting.dto.response.MeetingDetailResponse.RestaurantResponse;
import com.leteatgo.domain.meeting.dto.response.MeetingListResponse;
import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.NumberExpression;

import static com.leteatgo.domain.chat.entity.QChatRoom.chatRoom;
import static com.leteatgo.domain.meeting.entity.QMeeting.meeting;
import static com.leteatgo.domain.meeting.entity.QMeetingParticipant.meetingParticipant;
import static com.leteatgo.domain.tastyrestaurant.entity.QTastyRestaurant.tastyRestaurant;

public class QuerydslUtil {

    public static ConstructorExpression<MeetingDetailResponse> meetingDetailProjection() {
        return Projections.constructor(MeetingDetailResponse.class,
                meetingProjection(),
                hostProjection(),
                Projections.list(meetingParticipantsProjection()),
                restaurantProjection(),
                chatRoomProjection());
    }

    public static ConstructorExpression<MeetingResponse> meetingProjection() {
        return Projections.constructor(MeetingResponse.class,
                meeting.id,
                meeting.name,
                meeting.minParticipants,
                meeting.maxParticipants,
                meeting.currentParticipants,
                meeting.startDateTime,
                meeting.description,
                meeting.meetingOptions.status);
    }

    public static ConstructorExpression<HostResponse> hostProjection() {
        return Projections.constructor(HostResponse.class,
                meeting.host.id,
                meeting.host.nickname,
                meeting.host.profileImage);
    }

    public static ConstructorExpression<ParticipantResponse> meetingParticipantsProjection() {
        return Projections.constructor(ParticipantResponse.class,
                meetingParticipant.member.id,
                meetingParticipant.member.nickname,
                meetingParticipant.member.profileImage);
    }

    public static ConstructorExpression<RestaurantResponse> restaurantProjection() {
        return Projections.constructor(RestaurantResponse.class,
                tastyRestaurant.id,
                tastyRestaurant.name,
                tastyRestaurant.category,
                tastyRestaurant.roadAddress,
                tastyRestaurant.phoneNumber);
    }

    public static NumberExpression<Integer> chatRoomProjection() {
        return chatRoom.id.intValue();
    }

    public static ConstructorExpression<MeetingListResponse> meetingListProjection() {
        return Projections.constructor(MeetingListResponse.class,
                meeting.id,
                meeting.name,
                meeting.minParticipants,
                meeting.maxParticipants,
                meeting.currentParticipants,
                meeting.startDateTime,
                meeting.createdAt,
                meeting.description,
                meeting.meetingOptions.status,
                restaurantProjection()
        );
    }
}