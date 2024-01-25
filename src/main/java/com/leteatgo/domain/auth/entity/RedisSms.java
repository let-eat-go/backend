package com.leteatgo.domain.auth.entity;

import static com.leteatgo.global.exception.ErrorCode.ALREADY_VERIFIED;
import static com.leteatgo.global.exception.ErrorCode.WRONG_AUTH_CODE;

import com.leteatgo.domain.auth.exception.AuthException;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import lombok.Getter;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@RedisHash(value = "sms")
public class RedisSms {

    private static final long AUTH_CODE_TTL = 180L; // 3분

    @Id
    private final String id; // phone number
    @Indexed
    private final String authCode;
    @TimeToLive
    private Long expiration;
    private boolean isVerified;

    public RedisSms(String id, String authCode) {
        this.id = id;
        this.authCode = authCode;
        this.expiration = AUTH_CODE_TTL;
        this.isVerified = false;
    }

    public void validateAuthCode(String authCode) {
        if (this.isVerified) {
            throw new AuthException(ALREADY_VERIFIED);
        }
        if (!this.authCode.equals(authCode)) {
            throw new AuthException(WRONG_AUTH_CODE);
        }
    }

    public void verify() {
        this.isVerified = true;
    }
}
