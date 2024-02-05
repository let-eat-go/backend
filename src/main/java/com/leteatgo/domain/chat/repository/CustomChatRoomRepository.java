package com.leteatgo.domain.chat.repository;

import com.leteatgo.domain.chat.entity.ChatRoom;
import java.util.Optional;

public interface CustomChatRoomRepository {

    Optional<ChatRoom> findChatRoomFetch(Long id);
}
