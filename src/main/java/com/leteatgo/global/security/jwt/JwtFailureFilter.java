package com.leteatgo.global.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leteatgo.domain.auth.exception.TokenException;
import com.leteatgo.domain.notification.exception.NotificationException;
import com.leteatgo.global.exception.ErrorCode;
import com.leteatgo.global.exception.dto.ErrorResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtFailureFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        try {
            filterChain.doFilter(request, response);
        } catch (TokenException e) {
            sendErrorResponse(e.getErrorCode(), response);
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
