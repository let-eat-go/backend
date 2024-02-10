package com.leteatgo.domain.auth.service;

import static com.leteatgo.global.exception.ErrorCode.ALREADY_EXIST_EMAIL;
import static com.leteatgo.global.exception.ErrorCode.ALREADY_VERIFIED;
import static com.leteatgo.global.exception.ErrorCode.EXPIRED_AUTH_CODE;
import static com.leteatgo.global.exception.ErrorCode.WRONG_AUTH_CODE;
import static com.leteatgo.global.exception.ErrorCode.WRONG_PASSWORD;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.leteatgo.domain.auth.dto.request.EmailCheckRequest;
import com.leteatgo.domain.auth.dto.request.SignInRequest;
import com.leteatgo.domain.auth.dto.request.SignUpRequest;
import com.leteatgo.domain.auth.dto.request.SmsVerifyRequest;
import com.leteatgo.domain.auth.dto.response.SignInResponse;
import com.leteatgo.domain.auth.dto.response.SignUpResponse;
import com.leteatgo.domain.auth.entity.RedisSms;
import com.leteatgo.domain.auth.entity.RedisToken;
import com.leteatgo.domain.auth.exception.AuthException;
import com.leteatgo.domain.auth.repository.RedisSmsRepository;
import com.leteatgo.domain.auth.repository.RedisTokenRepository;
import com.leteatgo.domain.member.entity.Member;
import com.leteatgo.domain.member.repository.MemberRepository;
import com.leteatgo.domain.member.type.LoginType;
import com.leteatgo.domain.member.type.MemberRole;
import com.leteatgo.global.security.CustomUserDetailService;
import com.leteatgo.global.security.jwt.JwtTokenProvider;
import java.util.Optional;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    MemberRepository memberRepository;

    @Mock
    RedisSmsRepository redisSmsRepository;

    @Mock
    RedisTokenRepository redisTokenRepository;

    @Mock
    TokenService tokenService;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    JwtTokenProvider jwtTokenProvider;

    @Mock
    CustomUserDetailService customUserDetailService;

    @Mock
    UserDetails userDetails;

    @InjectMocks
    AuthService authService;

    @Nested
    @DisplayName("SignUp 메서드는")
    class SignUpMethod {

        SignUpRequest request = new SignUpRequest("test@naver.com", "testnick", "1!qweqwe",
                "1!qweqwe", "01012345678", "123456");
        RedisSms redisSms = new RedisSms("01012345678", "123456");
        Member mockMember = createTestMember(1L, "test@naver.com", "testnick", "1!qweqwe",
                "01012345678", LoginType.LOCAL, MemberRole.ROLE_USER);

        @Test
        @DisplayName("회원가입 성공하면 생성된 회원의 id를 반환한다.")
        void signUp() {
            // given
            given(passwordEncoder.encode(request.password())).willReturn("encodedPassword");
            given(redisSmsRepository.findById(request.phoneNumber())).willReturn(
                    Optional.ofNullable(redisSms));
            given(memberRepository.save(any(Member.class))).willReturn(mockMember);

            // when
            SignUpResponse response = authService.signUp(request);

            // then
            assertThat(response.id()).isNotNull();
        }

        @Test
        @DisplayName("비밀번호와 비밀번호 확인이 일치하지 않으면 예외를 발생시킨다.")
        void signUpWithWrongPasswordCheck() {
            // given
            SignUpRequest request = new SignUpRequest("test@naver.com", "testnick", "1!qweqwe",
                    "1!qweqwe2", "01012345678", "123456");

            // when
            // then
            assertThatThrownBy(() -> authService.signUp(request))
                    .isInstanceOf(AuthException.class)
                    .hasMessageContaining(WRONG_PASSWORD.getErrorMessage());
        }

        @Test
        @DisplayName("redisSmsRepository에서 핸드폰 번호를 찾을 수 없으면 예외를 발생시킨다.")
        void signUpWithWrongPhoneNumber() {
            // given
            given(redisSmsRepository.findById(request.phoneNumber())).willReturn(
                    Optional.empty());

            // when
            // then
            assertThatThrownBy(() -> authService.signUp(request))
                    .isInstanceOf(AuthException.class)
                    .hasMessageContaining(EXPIRED_AUTH_CODE.getErrorMessage());
        }
    }

    @Nested
    @DisplayName("SignIn 메서드는")
    class SignInMethod {

        SignInRequest request = new SignInRequest("test@naver.com", "1!qweqwe");
        RedisToken token = new RedisToken(1L, "refreshToken", "accessToken");

        @Test
        @DisplayName("로그인 성공하면 토큰을 반환한다.")
        void signIn() {
            // given
            given(customUserDetailService.loadUserByUsername(request.email())).willReturn(
                    userDetails);
            given(userDetails.getUsername()).willReturn("1");
            given(userDetails.getPassword()).willReturn("encodedPassword");
            given(passwordEncoder.matches(request.password(), userDetails.getPassword()))
                    .willReturn(true);
            given(jwtTokenProvider.createAccessToken(any(Authentication.class))).willReturn(
                    "accessToken");
            given(jwtTokenProvider.createRefreshToken(any(Authentication.class))).willReturn(
                    "refreshToken");
            given(redisTokenRepository.save(any(RedisToken.class))).willReturn(token);

            // when
            SignInResponse response = authService.signIn(request);

            // then
            assertThat(response.accessToken()).isNotNull();
        }

        @Test
        @DisplayName("비밀번호가 일치하지 않으면 예외를 발생시킨다.")
        void signInWithWrongPassword() {
            // given
            given(customUserDetailService.loadUserByUsername(request.email())).willReturn(
                    userDetails);
            given(userDetails.getPassword()).willReturn("encodedPassword");
            given(passwordEncoder.matches(request.password(), userDetails.getPassword()))
                    .willReturn(false);

            // when
            // then
            assertThatThrownBy(() -> authService.signIn(request))
                    .isInstanceOf(AuthException.class)
                    .hasMessageContaining(WRONG_PASSWORD.getErrorMessage());
        }

    }

    @Nested
    @DisplayName("CheckEmail 메서드는")
    class CheckEmailMethod {

        EmailCheckRequest request = new EmailCheckRequest("test@naver.com");

        @Test
        @DisplayName("이메일 중복검사를 통과하면 예외를 발생시키지 않는다.")
        void checkEmail() {
            // given
            given(memberRepository.existsByEmail(request.email())).willReturn(false);

            // when
            // then
            assertThatCode(() -> authService.checkEmail(request))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("이미 존재하는 이메일이면 예외를 발생시킨다.")
        void checkEmailWithDuplicatedEmail() {
            // given
            given(memberRepository.existsByEmail(request.email())).willReturn(true);

            // when
            // then
            assertThatThrownBy(() -> authService.checkEmail(request))
                    .isInstanceOf(AuthException.class)
                    .hasMessageContaining(ALREADY_EXIST_EMAIL.getErrorMessage());
        }

    }

    @Disabled
    @Nested
    @DisplayName("VerifySmsAuthCode 메서드는")
    class VerifySmsAuthCodeMethod {

        SmsVerifyRequest request = new SmsVerifyRequest("01012345678", "123456");

        @Test
        @DisplayName("핸드폰 인증번호 검증을 통과하면 예외를 발생시키지 않는다.")
        void verifySmsAuthCode() {
            // given
            RedisSms redisSms = new RedisSms("01012345678", "123456");
            given(redisSmsRepository.findById(anyString())).willReturn(
                    Optional.ofNullable(redisSms));

            // when
            // then
            assertThatCode(() -> authService.verifySmsAuthCode(request))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("이미 인증된 핸드폰 번호이면 예외를 발생시킨다.")
        void verifySmsAuthCodeWithAlreadyVerifiedPhoneNumber() {
            // given
            RedisSms redisSms = new RedisSms("01012345678", "123456");
            redisSms.verify();
            given(redisSmsRepository.findById(anyString())).willReturn(
                    Optional.ofNullable(redisSms));

            // when
            // then
            assertThatThrownBy(() -> authService.verifySmsAuthCode(request))
                    .isInstanceOf(AuthException.class)
                    .hasMessageContaining(ALREADY_VERIFIED.getErrorMessage());
        }

        @Test
        @DisplayName("인증번호가 일치하지 않으면 예외를 발생시킨다.")
        void verifySmsAuthCodeWithWrongAuthCode() {
            // given
            RedisSms redisSms = new RedisSms("01012345678", "111111");
            given(redisSmsRepository.findById(anyString())).willReturn(
                    Optional.ofNullable(redisSms));

            // when
            // then
            assertThatThrownBy(() -> authService.verifySmsAuthCode(request))
                    .isInstanceOf(AuthException.class)
                    .hasMessageContaining(WRONG_AUTH_CODE.getErrorMessage());
        }

        @Test
        @DisplayName("redisSmsRepository에서 핸드폰 번호를 찾을 수 없으면 예외를 발생시킨다.")
        void verifySmsAuthCodeWithWrongPhoneNumber() {
            // given
            given(redisSmsRepository.findById(anyString())).willReturn(Optional.empty());

            // when
            // then
            assertThatThrownBy(() -> authService.verifySmsAuthCode(request))
                    .isInstanceOf(AuthException.class)
                    .hasMessageContaining(EXPIRED_AUTH_CODE.getErrorMessage());
        }
    }

    @Test
    @DisplayName("signOut 메서드는 성공하면 토큰을 삭제한다.")
    void logout() {
        // given
        given(userDetails.getUsername()).willReturn("1");

        // when
        authService.signOut(Long.valueOf(userDetails.getUsername()));

        // then
        verify(tokenService, times(1)).deleteToken(Long.valueOf(userDetails.getUsername()));
    }

    private Member createTestMember(Long id, String email, String nickname, String password,
            String phoneNumber, LoginType loginType, MemberRole role) {
        Member member = Member.builder()
                .email(email)
                .nickname(nickname)
                .password(password)
                .phoneNumber(phoneNumber)
                .loginType(loginType)
                .role(role)
                .build();

        ReflectionTestUtils.setField(member, "id", id);

        return member;
    }

}