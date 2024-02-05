package com.leteatgo.domain.chat.repository;

import com.leteatgo.domain.chat.entity.ChatRoom;
import com.leteatgo.domain.meeting.entity.Meeting;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long>,
        CustomChatRoomRepository {

    Optional<ChatRoom> findByMeeting(Meeting meeting);

}
