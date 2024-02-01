package com.leteatgo.domain.chat.repository;

import com.leteatgo.domain.chat.entity.ChatMessage;
import com.leteatgo.domain.chat.entity.ChatRoom;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface CustomChatMessageRepository {

    Slice<ChatMessage> findByChatRoomFetch(ChatRoom chatRoom, Pageable pageable);
}
