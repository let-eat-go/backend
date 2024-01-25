package com.leteatgo.domain.auth.controller;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static com.leteatgo.global.exception.ErrorCode.ALREADY_EXIST_EMAIL;
import static com.leteatgo.global.exception.ErrorCode.ALREADY_EXIST_PHONE_NUMBER;
import static com.leteatgo.global.exception.ErrorCode.ALREADY_VERIFIED;
import static com.leteatgo.global.exception.ErrorCode.PHONE_NUMBER_NOT_VERIFIED;
import static com.leteatgo.global.exception.ErrorCode.WRONG_AUTH_CODE;
import static com.leteatgo.global.exception.ErrorCode.WRONG_PASSWORD;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceDocumentation;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leteatgo.domain.auth.dto.request.EmailCheckRequest;
import com.leteatgo.domain.auth.dto.request.SignInRequest;
import com.leteatgo.domain.auth.dto.request.SignUpRequest;
import com.leteatgo.domain.auth.dto.request.SmsSendRequest;
import com.leteatgo.domain.auth.dto.request.SmsVerifyRequest;
import com.leteatgo.domain.auth.dto.response.SignUpResponse;
import com.leteatgo.domain.auth.exception.AuthException;
import com.leteatgo.domain.auth.service.AuthService;
import com.leteatgo.domain.auth.service.SmsSender;
import com.leteatgo.global.security.jwt.JwtTokenProvider;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureRestDocs
@WebMvcTest(AuthController.class)
@WithMockUser(username = "mockUser", roles = "USER")
class AuthControllerTest {

    @MockBean
    AuthService authService;

    @MockBean
    SmsSender smsSender;

    @MockBean
    JwtTokenProvider jwtTokenProvider;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mockMvc;

    @Nested
    @DisplayName("이메일 중복검사")
    class CheckEmailTests {

        EmailCheckRequest request = new EmailCheckRequest("test@naver.com");
        String requestBody = objectMapper.writeValueAsString(request);

        CheckEmailTests() throws JsonProcessingException {
        }

        @Test
        @DisplayName("[성공] 이메일 중복 검사")
        void checkEmail() throws Exception {
            // given
            // when
            doNothing().when(authService).checkEmail(request);

            // then
            mockMvc.perform(post("/api/auth/check-email")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andDo(document("이메일 중복 검사",
                            resource(ResourceSnippetParameters.builder()
                                    .tag("auth")
                                    .summary("이메일 중복 검사")
                                    .build()
                            )
                    ));

            verify(authService, times(1)).checkEmail(any(EmailCheckRequest.class));
        }

        @Test
        @DisplayName("[실패] 이미 존재하는 이메일")
        void checkEmail_AlreadyExist() throws Exception {
            // given
            // when
            doThrow(new AuthException(ALREADY_EXIST_EMAIL)).when(authService)
                    .checkEmail(request);

            // then
            mockMvc.perform(post("/api/auth/check-email")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andDo(document("이미 존재하는 이메일",
                            resource(ResourceSnippetParameters.builder()
                                    .tag("auth")
                                    .summary("이메일 중복 검사")
                                    .build()
                            )
                    ));

            verify(authService, times(1)).checkEmail(any(EmailCheckRequest.class));
        }
    }

    @Test
    @DisplayName("[성공] 핸드폰 인증번호 발송")
    void sendSms() throws Exception {
        // given
        SmsSendRequest request = new SmsSendRequest("01012345678");
        String requestBody = objectMapper.writeValueAsString(request);

        // when
        doNothing().when(smsSender).sendSms(request);

        // then
        mockMvc.perform(post("/api/auth/send-sms")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("핸드폰 인증번호 발송",
                        resource(ResourceSnippetParameters.builder()
                                .tag("auth")
                                .summary("핸드폰 인증번호 발송")
                                .build()
                        )
                ));

        verify(smsSender, times(1)).sendSms(request);
    }

    @Nested
    @DisplayName("핸드폰 인증번호 확인")
    class verifySmsTests {

        SmsVerifyRequest request = new SmsVerifyRequest("01012345678", "123456");
        String requestBody = objectMapper.writeValueAsString(request);

        verifySmsTests() throws JsonProcessingException {
        }

        @Test
        @DisplayName("[성공] 핸드폰 인증번호 확인")
        void verifySms() throws Exception {
            // given
            // when
            doNothing().when(authService).verifySmsAuthCode(request);

            // then
            mockMvc.perform(put("/api/auth/verify-sms")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andDo(document("핸드폰 인증 성공",
                            resource(ResourceSnippetParameters.builder()
                                    .tag("auth")
                                    .summary("핸드폰 인증번호 확인")
                                    .build()
                            )
                    ));

            verify(authService, times(1)).verifySmsAuthCode(request);
        }

        @Test
        @DisplayName("[실패] 핸드폰 인증번호 확인 - 인증번호가 일치하지 않음")
        void verifySms_WrongAuthCode() throws Exception {
            // given
            // when
            doThrow(new AuthException(WRONG_AUTH_CODE)).when(authService)
                    .verifySmsAuthCode(request);

            // then
            mockMvc.perform(put("/api/auth/verify-sms")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andDo(document("인증번호가 일치하지 않음",
                            resource(ResourceSnippetParameters.builder()
                                    .tag("auth")
                                    .summary("핸드폰 인증번호 확인")
                                    .build()
                            )
                    ));

            verify(authService, times(1)).verifySmsAuthCode(request);
        }

        @Test
        @DisplayName("[실패] 핸드폰 인증번호 확인 - 이미 인증된 핸드폰 번호")
        void verifySms_AlreadyVerified() throws Exception {
            // given
            // when
            doThrow(new AuthException(ALREADY_VERIFIED)).when(authService)
                    .verifySmsAuthCode(request);

            // then
            mockMvc.perform(put("/api/auth/verify-sms")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andDo(document("이미 인증된 핸드폰 번호",
                            resource(ResourceSnippetParameters.builder()
                                    .tag("auth")
                                    .summary("핸드폰 인증번호 확인")
                                    .build()
                            )
                    ));

            verify(authService, times(1)).verifySmsAuthCode(request);
        }
    }

    @Nested
    @DisplayName("로컬 회원가입")
    class SignUpTests {

        SignUpRequest request = new SignUpRequest("test@naver.com", "testnick", "1!qweqwe",
                "1!qweqwe", "01012345678");
        String requestBody = objectMapper.writeValueAsString(request);
        SignUpResponse response = new SignUpResponse(1L);

        SignUpTests() throws JsonProcessingException {
        }

        @Test
        @DisplayName("[성공] 로컬 회원가입")
        void signUp() throws Exception {
            // given
            // when
            doReturn(response).when(authService).signUp(request);
            // then
            mockMvc.perform(post("/api/auth/signup")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andDo(document("로컬 회원가입",
                            resource(ResourceSnippetParameters.builder()
                                    .tag("auth")
                                    .summary("로컬 회원가입")
                                    .responseHeaders(
                                            ResourceDocumentation.headerWithName("Location")
                                                    .description("회원가입된 회원의 URI")
                                    )
                                    .build()
                            )
                    ));

            verify(authService, times(1)).signUp(any(SignUpRequest.class));
        }

        @Test
        @DisplayName("[실패] 로컬 회원가입 - 비밀번호 불일치")
        void signUp_AlreadyExistEmail() throws Exception {
            // given
            // when
            doThrow(new AuthException(WRONG_PASSWORD)).when(authService)
                    .signUp(request);
            // then
            mockMvc.perform(post("/api/auth/signup")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andDo(document("비밀번호 불일치",
                            resource(ResourceSnippetParameters.builder()
                                    .tag("auth")
                                    .summary("로컬 회원가입")
                                    .build()
                            )
                    ));

            verify(authService, times(1)).signUp(any(SignUpRequest.class));
        }

        @Test
        @DisplayName("[실패] 로컬 회원가입 - 이미 가입된 핸드폰 번호")
        void signUp_WrongAuthCode() throws Exception {
            // given
            // when
            doThrow(new AuthException(ALREADY_EXIST_PHONE_NUMBER)).when(authService)
                    .signUp(request);
            // then
            mockMvc.perform(post("/api/auth/signup")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andDo(document("이미 가입된 핸드폰 번호",
                            resource(ResourceSnippetParameters.builder()
                                    .tag("auth")
                                    .summary("로컬 회원가입")
                                    .build()
                            )
                    ));

            verify(authService, times(1)).signUp(any(SignUpRequest.class));
        }

        @Test
        @DisplayName("[실패] 로컬 회원가입 - 아직 인증되지 않은 핸드폰 번호")
        void signUp_AlreadyVerified() throws Exception {
            // given
            // when
            doThrow(new AuthException(PHONE_NUMBER_NOT_VERIFIED)).when(authService)
                    .signUp(request);
            // then
            mockMvc.perform(post("/api/auth/signup")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isUnauthorized())
                    .andDo(print())
                    .andDo(document("아직 인증되지 않은 핸드폰 번호",
                            resource(ResourceSnippetParameters.builder()
                                    .tag("auth")
                                    .summary("로컬 회원가입")
                                    .build()
                            )
                    ));

            verify(authService, times(1)).signUp(any(SignUpRequest.class));
        }
    }

    @Nested
    @DisplayName("로컬 로그인")
    class SignInTests {

        SignInRequest request = new SignInRequest("test@naver.com", "1!qweqwe");
        String requestBody = objectMapper.writeValueAsString(request);
        String token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiI4Iiwicm9sZSI6IlJPTEVfVVNFUiIsImlhdCI6MTcwNjA3NDI5MywiZXhwIjoxNzA4NjY2MjkzLCJpc3MiOiJsZXRlYXRnbyJ9.pRsIGFVZQ8p_WX-Zw2Rf5eQNwfK1xHKA-UFDczzQzt4";

        SignInTests() throws JsonProcessingException {
        }

        @Test
        @DisplayName("[성공] 로컬 로그인에 성공하면 토큰을 발급하고 쿠키에 저장")
        void signIn() throws Exception {
            // given
            // when
            doReturn(token).when(authService).signIn(request);
            // then
            mockMvc.perform(post("/api/auth/signin")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andDo(document("로컬 로그인",
                            resource(ResourceSnippetParameters.builder()
                                    .tag("auth")
                                    .summary("로컬 로그인")
                                    .build()
                            )
                    ));
        }

        @Test
        @DisplayName("[실패] 로컬 로그인 - 비밀번호 불일치")
        void signIn_WrongPassword() throws Exception {
            // given
            // when
            doThrow(new AuthException(WRONG_PASSWORD)).when(authService)
                    .signIn(request);
            // then
            mockMvc.perform(post("/api/auth/signin")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andDo(document("비밀번호 불일치",
                            resource(ResourceSnippetParameters.builder()
                                    .tag("auth")
                                    .summary("로컬 로그인")
                                    .build()
                            )
                    ));
        }
    }

    @Test
    @DisplayName("[성공] 로그아웃")
    void signOut() throws Exception {
        // given
        UserDetails userDetails = mock(UserDetails.class);
        // when
        doNothing().when(authService).signOut(userDetails);
        // then
        mockMvc.perform(delete("/api/auth/signout")
                        .cookie(new Cookie("access_token", "token"))
                        .with(csrf())
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("로그아웃",
                        resource(ResourceSnippetParameters.builder()
                                .tag("auth")
                                .summary("로그아웃")
                                .build()
                        )
                ));
    }

}