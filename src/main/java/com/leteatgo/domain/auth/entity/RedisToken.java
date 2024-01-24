package com.leteatgo.domain.auth.entity;


import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@AllArgsConstructor
@RedisHash(value = "token", timeToLive = 60 * 60 * 24 * 7)
public class RedisToken {

    @Id
    private Long id;
    private String refreshToken;
    @Indexed
    private String accessToken;

    public void updateAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
