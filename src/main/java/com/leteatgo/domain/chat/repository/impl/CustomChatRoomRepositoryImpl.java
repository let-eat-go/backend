package com.leteatgo.domain.chat.repository.impl;

import static com.leteatgo.domain.chat.entity.QChatRoom.chatRoom;
import static com.leteatgo.domain.meeting.entity.QMeeting.meeting;
import static com.leteatgo.domain.meeting.entity.QMeetingParticipant.meetingParticipant;
import static com.leteatgo.domain.member.entity.QMember.member;
import static com.leteatgo.domain.region.entity.QRegion.region;

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
                .join(meeting.region, region).fetchJoin()
                .join(meeting.meetingParticipants, meetingParticipant).fetchJoin()
                .leftJoin(meetingParticipant.member, member).fetchJoin()
                .where(chatRoom.id.eq(id))
                .fetchOne());
    }
}
