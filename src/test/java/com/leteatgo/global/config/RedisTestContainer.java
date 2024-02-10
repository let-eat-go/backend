package com.leteatgo.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

@ActiveProfiles("test")
@Configuration
public class RedisTestContainer {

    private static final String REDIS_DOCKER_IMAGE = "redis:alpine";
    private static final Integer PORT = 6379;

    static {
        GenericContainer<?> redis =
                new GenericContainer<>(DockerImageName.parse(REDIS_DOCKER_IMAGE))
                        .withExposedPorts(PORT)
                        .withReuse(true);

        redis.start();

        System.setProperty("spring.data.redis.host", redis.getHost());
        System.setProperty("spring.data.redis.port", redis.getMappedPort(PORT).toString());
    }
}
