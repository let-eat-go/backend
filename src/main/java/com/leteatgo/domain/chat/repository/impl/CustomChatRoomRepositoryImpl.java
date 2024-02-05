package com.leteatgo.domain.chat.repository.impl;

import static com.leteatgo.domain.chat.entity.QChatRoom.chatRoom;
import static com.leteatgo.domain.meeting.entity.QMeeting.meeting;
import static com.leteatgo.domain.meeting.entity.QMeetingParticipant.meetingParticipant;

import com.leteatgo.domain.chat.entity.ChatRoom;
import com.leteatgo.domain.chat.repository.CustomChatRoomRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomChatRoomRepositoryImpl implements CustomChatRoomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<ChatRoom> findChatRoomFetch(Long id) {
        return Optional.ofNullable(queryFactory.selectFrom(chatRoom)
                .join(chatRoom.meeting, meeting).fetchJoin()
                .join(meeting.meetingParticipants, meetingParticipant).fetchJoin()
                .where(chatRoom.id.eq(id))
                .fetchOne());
    }
}
