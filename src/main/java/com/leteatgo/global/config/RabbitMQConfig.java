//package com.leteatgo.global.config;
//
//import com.leteatgo.global.constants.RoutingKey;
//import org.springframework.amqp.core.Binding;
//import org.springframework.amqp.core.BindingBuilder;
//import org.springframework.amqp.core.Queue;
//import org.springframework.amqp.core.TopicExchange;
//import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
//import org.springframework.amqp.rabbit.connection.ConnectionFactory;
//import org.springframework.amqp.rabbit.core.RabbitTemplate;
//import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class RabbitMQConfig {
//
//    @Value("${spring.rabbitmq.host}")
//    private String host;
//
//    @Value("${spring.rabbitmq.port}")
//    private int port;
//
//    @Value("${spring.rabbitmq.username}")
//    private String username;
//
//    @Value("${spring.rabbitmq.password}")
//    private String password;
//
//    private static final String EXCHANGE_NAME = "topic.exchange";
//    private static final String CHAT_QUEUE_NAME = "chat.queue";
//
//    @Bean
//    public ConnectionFactory connectionFactory() {
//        CachingConnectionFactory factory = new CachingConnectionFactory();
//        factory.setHost(host);
//        factory.setPort(port);
//        factory.setUsername(username);
//        factory.setPassword(password);
//        return factory;
//    }
//
//    @Bean
//    public Jackson2JsonMessageConverter jsonMessageConverter() {
//        return new Jackson2JsonMessageConverter();
//    }
//
//    @Bean
//    public RabbitTemplate rabbitTemplate() {
//        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory());
//        rabbitTemplate.setMessageConverter(jsonMessageConverter());
//        return rabbitTemplate;
//    }
//
//    @Bean
//    public TopicExchange exchange() {
//        return new TopicExchange(EXCHANGE_NAME);
//    }
//
//    @Bean
//    public Queue chatQueue() {
//        return new Queue(CHAT_QUEUE_NAME, true);
//    }
//
//    @Bean
//    public Binding chatBinding() {
//        return BindingBuilder.bind(chatQueue())
//                .to(exchange())
//                .with(RoutingKey.CHAT_ROUTING_KEY + "#");
//    }
//}
