package com.leteatgo.domain.chat.repository;

import com.leteatgo.domain.chat.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long>,
        CustomChatMessageRepository {
}
