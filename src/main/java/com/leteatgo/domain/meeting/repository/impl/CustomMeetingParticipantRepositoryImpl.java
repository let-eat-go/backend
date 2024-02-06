package com.leteatgo.domain.meeting.repository.impl;

import static com.leteatgo.domain.chat.entity.QChatMessage.chatMessage;
import static com.leteatgo.domain.chat.entity.QChatRoom.chatRoom;
import static com.leteatgo.domain.meeting.entity.QMeeting.meeting;
import static com.leteatgo.domain.meeting.entity.QMeetingParticipant.meetingParticipant;

import com.leteatgo.domain.chat.dto.response.MyChatRoomResponse;
import com.leteatgo.domain.chat.dto.response.MyChatRoomResponse.Chat;
import com.leteatgo.domain.chat.entity.QChatMessage;
import com.leteatgo.domain.chat.type.RoomStatus;
import com.leteatgo.domain.meeting.repository.CustomMeetingParticipantRepository;
import com.leteatgo.domain.member.entity.Member;
import com.leteatgo.global.util.SliceUtil;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.DatePath;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

@RequiredArgsConstructor
public class CustomMeetingParticipantRepositoryImpl implements CustomMeetingParticipantRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<MyChatRoomResponse> findAllMyChatRooms(Member member, Pageable pageable) {
        DatePath<LocalDateTime> createdAt =
                Expressions.datePath(LocalDateTime.class, "createdAt");

        QChatMessage cm = new QChatMessage("cm");

        JPQLQuery<Long> maxId = JPAExpressions.select(cm.id.max())
                .from(cm)
                .where(cm.chatRoom.eq(chatRoom));

        List<MyChatRoomResponse> contents = queryFactory
                .select(Projections.constructor(MyChatRoomResponse.class,
                        meeting.name,
                        meeting.restaurantCategory,
                        meeting.region.name,
                        Projections.constructor(Chat.class,
                                chatRoom.id,
                                ExpressionUtils.as(
                                        JPAExpressions.select(cm.content)
                                                .from(cm)
                                                .where(cm.id.eq(maxId)), "content"),
                                ExpressionUtils.as(
                                        JPAExpressions.select(cm.isRead)
                                                .from(cm)
                                                .where(cm.id.eq(maxId)), "read"),
                                ExpressionUtils.as(
                                        JPAExpressions.select(cm.createdAt)
                                                .from(cm)
                                                .where(cm.id.eq(maxId)), "createdAt")
                        )
                ))
                .from(meetingParticipant)
                .join(meetingParticipant.meeting, meeting)
                .join(meeting.chatRoom, chatRoom)
                .join(chatRoom.chatMessages, chatMessage)
                .where(meetingParticipant.member.eq(member),
                        chatRoom.status.eq(RoomStatus.OPEN))
                .groupBy(meeting.id)
                .orderBy(createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        return new SliceUtil<>(contents, pageable).getSlice();
    }
}
