package com.leteatgo.domain.meeting.repository.impl;

import static com.leteatgo.domain.chat.entity.QChatRoom.chatRoom;
import static com.leteatgo.domain.meeting.entity.QMeeting.meeting;
import static com.leteatgo.domain.meeting.entity.QMeetingParticipant.meetingParticipant;
import static com.leteatgo.domain.member.entity.QMember.member;
import static com.leteatgo.domain.tastyrestaurant.entity.QTastyRestaurant.tastyRestaurant;

import com.leteatgo.domain.meeting.dto.response.MeetingDetailResponse;
import com.leteatgo.domain.meeting.dto.response.MeetingDetailResponse.HostResponse;
import com.leteatgo.domain.meeting.dto.response.MeetingDetailResponse.MeetingResponse;
import com.leteatgo.domain.meeting.dto.response.MeetingDetailResponse.ParticipantResponse;
import com.leteatgo.domain.meeting.dto.response.MeetingDetailResponse.RestaurantResponse;
import com.leteatgo.domain.meeting.repository.CustomMeetingRepository;
import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomMeetingRepositoryImpl implements CustomMeetingRepository {

    private final JPAQueryFactory queryFactory;


    @Override
    public Optional<MeetingDetailResponse> findMeetingDetail(Long meetingId) {

        MeetingDetailResponse meetingDetailResponse = queryFactory.select(meetingDetailProjection())
                .from(meeting)
                .join(meeting.host, member)
                .join(meeting.chatRoom, chatRoom)
                .leftJoin(meeting.tastyRestaurant, tastyRestaurant)
                .leftJoin(meeting.meetingParticipants, meetingParticipant)
                .where(meeting.id.eq(meetingId))
                .fetchOne();

        return Optional.ofNullable(meetingDetailResponse);
    }

    private ConstructorExpression<MeetingDetailResponse> meetingDetailProjection() {
        return Projections.constructor(MeetingDetailResponse.class,
                meetingProjection(),
                hostProjection(),
                Projections.list(meetingParticipantsProjection()),
                restaurantProjection(),
                chatRoomProjection());
    }

    private ConstructorExpression<MeetingResponse> meetingProjection() {
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

    private ConstructorExpression<HostResponse> hostProjection() {
        return Projections.constructor(HostResponse.class,
                meeting.host.id,
                meeting.host.nickname,
                meeting.host.profileImage);
    }

    private ConstructorExpression<ParticipantResponse> meetingParticipantsProjection() {
        return Projections.constructor(ParticipantResponse.class,
                meetingParticipant.member.id,
                meetingParticipant.member.nickname,
                meetingParticipant.member.profileImage);
    }

    private ConstructorExpression<RestaurantResponse> restaurantProjection() {
        return Projections.constructor(RestaurantResponse.class,
                tastyRestaurant.id,
                tastyRestaurant.name,
                tastyRestaurant.roadAddress,
                tastyRestaurant.phoneNumber);
    }

    private NumberExpression<Integer> chatRoomProjection() {
        return chatRoom.id.intValue();
    }
}
