package com.leteatgo.domain.auth.service;

import static com.leteatgo.global.exception.ErrorCode.EMPTY_TOKEN;
import static com.leteatgo.global.exception.ErrorCode.EXPIRED_TOKEN;

import com.leteatgo.domain.auth.entity.RedisToken;
import com.leteatgo.domain.auth.exception.TokenException;
import com.leteatgo.domain.auth.repository.RedisTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TokenService {

    private final RedisTokenRepository redisTokenRepository;

    public RedisToken getTokenByAccessToken(String accessToken) {
        if (!StringUtils.hasText(accessToken)) {
            throw new TokenException(EMPTY_TOKEN);
        }
        return redisTokenRepository.findByAccessToken(accessToken)
                .orElseThrow(() -> new TokenException(EXPIRED_TOKEN));
    }

    @Transactional
    public void updateToken(RedisToken token, String accessToken) {
        token.updateAccessToken(accessToken);
        redisTokenRepository.save(token);
    }

    @Transactional
    public void deleteToken(Long memberId) {
        redisTokenRepository.deleteById(memberId.toString());
    }

}
