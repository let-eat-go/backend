package com.leteatgo.global.security.jwt;

import static com.leteatgo.global.exception.ErrorCode.EMPTY_TOKEN;
import static com.leteatgo.global.exception.ErrorCode.INTERNAL_ERROR;
import static com.leteatgo.global.util.CookieUtil.COOKIE_MAX_AGE;
import static com.leteatgo.global.util.CookieUtil.COOKIE_NAME;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leteatgo.domain.auth.exception.TokenException;
import com.leteatgo.global.exception.ErrorCode;
import com.leteatgo.global.exception.dto.ErrorResponse;
import com.leteatgo.global.util.CookieUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String[] WHITELIST = {
            "/docs/**", // swagger
            "/v3/api-docs/**", // swagger
            "/api/auth/signup", // 회원가입
            "/api/auth/signin", // 로그인
            "/api/auth/email-check", // 이메일 중복 체크
            "/api/auth/send-sms", // 인증번호 발송
            "/api/auth/verify-sms", // 인증번호 확인
    };

    private final JwtTokenProvider jwtTokenProvider;
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();
    private final ObjectMapper objectMapper = new ObjectMapper();


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();
        if (Arrays.stream(WHITELIST).anyMatch(pattern -> antPathMatcher.match(pattern, path))) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String accessToken = resolveToken(request);
            validateAndReissueToken(accessToken, response);
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            handleException(e, response);
        }
    }

    private String resolveToken(HttpServletRequest request) {
        return CookieUtil.getCookie(request, COOKIE_NAME)
                .map(Cookie::getValue)
                .orElseThrow(() -> new TokenException(EMPTY_TOKEN));
    }

    private void validateAndReissueToken(String accessToken, HttpServletResponse response) {
        if (jwtTokenProvider.validateToken(accessToken)) {
            setAuthentication(accessToken);
        } else {
            String reissueAccessToken = jwtTokenProvider.reissueAccessToken(accessToken);
            if (reissueAccessToken != null) {
                setAuthentication(reissueAccessToken);
                CookieUtil.addCookie(response, COOKIE_NAME, reissueAccessToken, COOKIE_MAX_AGE);
            }
        }
    }

    private void setAuthentication(String accessToken) {
        Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private void handleException(Exception e, HttpServletResponse response) throws IOException {
        if (e instanceof TokenException) {
            sendErrorResponse(((TokenException) e).getErrorCode(), response);
        } else {
            log.error("doFilterInternal : {}", e.getMessage());
            sendErrorResponse(INTERNAL_ERROR, response);
        }
    }


    private void sendErrorResponse(ErrorCode errorCode, HttpServletResponse response)
            throws IOException {
        ErrorResponse errorResponse = new ErrorResponse(errorCode, errorCode.getErrorMessage());
        response.setStatus(errorCode.getHttpStatus().value());
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }

}
