package com.leteatgo.domain.meeting.repository;

import com.leteatgo.domain.chat.dto.response.MyChatRoomResponse;
import com.leteatgo.domain.member.entity.Member;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface CustomMeetingParticipantRepository {

    Slice<MyChatRoomResponse> findAllMyChatRooms(Member member, Pageable pageable);
}
