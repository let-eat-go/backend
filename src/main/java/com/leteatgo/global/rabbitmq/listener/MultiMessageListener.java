package com.leteatgo.global.rabbitmq.listener;

import com.leteatgo.global.rabbitmq.handler.MessageHandler;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class MultiMessageListener implements MessageListener {

    private final List<MessageHandler> messageHandlers;

    @Override
    public void onMessage(Message message) {
        messageHandlers.stream()
                .filter(handler -> handler.canHandle(
                        message.getMessageProperties().getConsumerQueue()))
                .forEach(handler -> handler.handle(message));
    }
}
