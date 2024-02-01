package com.leteatgo.domain.meeting.repository.impl;

import static com.leteatgo.domain.chat.entity.QChatMessage.chatMessage;
import static com.leteatgo.domain.chat.entity.QChatRoom.chatRoom;
import static com.leteatgo.domain.meeting.entity.QMeeting.meeting;
import static com.leteatgo.domain.meeting.entity.QMeetingParticipant.meetingParticipant;

import com.leteatgo.domain.chat.dto.response.MyChatRoomResponse;
import com.leteatgo.domain.chat.entity.QChatMessage;
import com.leteatgo.domain.meeting.repository.CustomMeetingParticipantRepository;
import com.leteatgo.domain.member.entity.Member;
import com.leteatgo.global.util.SliceUtil;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.DatePath;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
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
    public Slice<MyChatRoomResponse> findByMemberFetch(Member member, Pageable pageable) {
        DatePath<LocalDateTime> createdAt =
                Expressions.datePath(LocalDateTime.class, "createdAt");

        QChatMessage cm2 = new QChatMessage("cm2");

        List<MyChatRoomResponse> contents = queryFactory
                .select(Projections.fields(MyChatRoomResponse.class,
                        meeting.name.as("meetingName"),
                        meeting.restaurantCategory.as("category"),
                        meeting.region,
                        chatRoom.id.as("roomId"),
                        chatMessage.id.as("messageId"),
                        chatMessage.content,
                        chatMessage.isRead,
                        ExpressionUtils.as(
                                JPAExpressions.select(cm2.createdAt.max())
                                        .from(cm2)
                                        .where(cm2.chatRoom.eq(chatRoom))
                                        .orderBy(cm2.createdAt.desc())
                                , "createdAt")
                ))
                .from(meetingParticipant)
                .join(meetingParticipant.meeting, meeting)
                .join(meeting.chatRoom, chatRoom)
                .join(chatRoom.chatMessages, chatMessage)
                .where(meetingParticipant.member.eq(member))
                .groupBy(meeting.id)
                .orderBy(createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        return new SliceUtil<>(contents, pageable).getSlice();
    }
}
