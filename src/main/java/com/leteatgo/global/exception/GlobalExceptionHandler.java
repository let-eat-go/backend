package com.leteatgo.global.exception;

import static com.leteatgo.global.exception.ErrorCode.INTERNAL_ERROR;
import static com.leteatgo.global.exception.ErrorCode.INVALID_REQUEST;

import com.leteatgo.global.exception.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private static final String INVALID_DTO_FIELD_ERROR_MESSAGE_FORMAT = "%s 필드의 %s (전달된 값: %s)";

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(HttpServletRequest request,
            CustomException e) {
        logError(request, e);
        return ErrorResponse.of(e.getErrorCode(), e.getMessage());
    }

    // Bean Validation(@Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleInvalidDtoField(MethodArgumentNotValidException e) {
        FieldError fieldError = e.getFieldErrors().get(0);
        String errorMessage = String.format(
                INVALID_DTO_FIELD_ERROR_MESSAGE_FORMAT,
                fieldError.getField(),
                fieldError.getDefaultMessage(),
                fieldError.getRejectedValue()
        );

        return ErrorResponse.of(INVALID_REQUEST, errorMessage);
    }

    // Parameter Missing(@RequestParam)
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingServletRequestParameterException(
            MissingServletRequestParameterException e) {
        String name = e.getParameterName();
        String errorMessage = String.format("요청 파라미터 %s가 누락되었습니다.", name);

        return ErrorResponse.of(INVALID_REQUEST, errorMessage);
    }

    // 지원하지 않는 HTTP Method 호출
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupportedException(
            HttpRequestMethodNotSupportedException e) {
        String errorMessage = String.format("해당 API는 %s 메서드를 지원하지 않습니다.", e.getMethod());

        return ErrorResponse.of(INVALID_REQUEST, errorMessage);
    }

    // 데이터 삽입/수정 시 무결성 제약 조건 위반
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolationException(
            DataIntegrityViolationException e) {
        return ErrorResponse.of(INVALID_REQUEST, e.getMessage());
    }

    // 제약 조건 위반
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleViolationException(ConstraintViolationException e) {
        return ErrorResponse.of(INVALID_REQUEST, e.getMessage());
    }

    // 기타 에러
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(HttpServletRequest request, Exception e) {
        logError(request, e);
        return ErrorResponse.of(INTERNAL_ERROR, e.getMessage());
    }

    private void logError(HttpServletRequest request, Exception e) {
        String requestUri = request.getRequestURI();
        String requestMethod = request.getMethod();

        log.error("[{}] {} | {}", requestMethod, requestUri, e.getMessage());
    }
}
