package com.leteatgo.domain.chat.event;

import com.leteatgo.domain.chat.event.dto.CloseChatRoomEvent;
import com.leteatgo.domain.chat.event.dto.CreateChatRoomEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChatRoomEventPublisher {

    private final ApplicationEventPublisher eventPublisher;

    public void publishCreateChatRoom(CreateChatRoomEvent event) {
        log.info("채팅방이 생성되었습니다. {}", event.meetingId());
        eventPublisher.publishEvent(event);
    }

    public void publishCloseChatRoom(CloseChatRoomEvent event) {
        log.info("채팅방이 종료되었습니다. {}", event.meetingId());
        eventPublisher.publishEvent(event);
    }
}
