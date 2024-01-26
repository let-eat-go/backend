package com.leteatgo.global.security.oauth.handler;

import static com.leteatgo.global.util.CookieUtil.COOKIE_MAX_AGE;
import static com.leteatgo.global.util.CookieUtil.COOKIE_NAME;

import com.leteatgo.domain.auth.entity.RedisToken;
import com.leteatgo.domain.auth.repository.RedisTokenRepository;
import com.leteatgo.global.security.jwt.JwtTokenProvider;
import com.leteatgo.global.util.CookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTokenRepository redisTokenRepository;

    private static final String URI = "/api/auth/oauth/success";

    @Override
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException {
        String accessToken = jwtTokenProvider.createAccessToken(authentication);
        String refreshToken = jwtTokenProvider.createRefreshToken(authentication);

        RedisToken token = new RedisToken(
                Long.valueOf(authentication.getName()),
                accessToken,
                refreshToken
        );
        redisTokenRepository.save(token);
        CookieUtil.addCookie(response, COOKIE_NAME, accessToken, COOKIE_MAX_AGE);

        response.sendRedirect(URI);

        // TODO: 서버 내부에서 리다이렉트 후 프론트에 응답 성공 메시지를 보내주기? 프론트 연동 시 수정
    }
}
