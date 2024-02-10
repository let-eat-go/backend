package com.leteatgo.global.rabbitmq.handler;

import static com.leteatgo.global.constants.Notification.NOTIFICATION_QUEUE;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leteatgo.domain.notification.dto.NotificationDto;
import com.leteatgo.domain.notification.service.SseEmitterService;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationMessageHandler implements MessageHandler {

    private final ObjectMapper objectMapper;
    private final SseEmitterService sseEmitterService;

    @Override
    public boolean canHandle(String queueName) {
        return queueName.startsWith(NOTIFICATION_QUEUE);
    }

    @Override
    public void handle(Message message) {
        try {
            String emitterId = message.getMessageProperties().getConsumerQueue()
                    .replace(NOTIFICATION_QUEUE, "");
            NotificationDto notificationDto = objectMapper.readValue(message.getBody(),
                    NotificationDto.class);
            sseEmitterService.sendNotification(emitterId, notificationDto);
        } catch (IOException e) {
            log.error("Cannot read notification message", e);
        }
    }
}
