package com.leteatgo.global.security.oauth.handler;

import static com.leteatgo.global.exception.ErrorCode.ACCESS_DENIED;
import static org.springframework.http.HttpStatus.FORBIDDEN;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leteatgo.global.exception.dto.ErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2FailureHandler implements AuthenticationFailureHandler {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception) throws IOException {
        log.error("소셜 로그인에 실패했습니다. 에러 메시지: {}", exception.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(ACCESS_DENIED,
                ACCESS_DENIED.getErrorMessage());
        response.setStatus(FORBIDDEN.value());
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }

}
