package com.leteatgo.global.rabbitmq.handler;

import org.springframework.amqp.core.Message;

public interface MessageHandler {

    boolean canHandle(String queueName);

    void handle(Message message);

}
