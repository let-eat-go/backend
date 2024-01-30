package com.leteatgo.global.config;

import com.leteatgo.global.security.handler.CustomAccessDeniedHandler;
import com.leteatgo.global.security.handler.CustomAuthenticationEntryPoint;
import com.leteatgo.global.security.jwt.JwtAuthenticationFilter;
import com.leteatgo.global.security.oauth.handler.CustomOAuth2FailureHandler;
import com.leteatgo.global.security.oauth.handler.CustomOAuth2SuccessHandler;
import com.leteatgo.global.security.oauth.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final CustomOAuth2SuccessHandler customOAuth2SuccessHandler;
    private final CustomOAuth2FailureHandler customOAuth2FailureHandler;
    private final CustomOAuth2UserService customOAuth2UserService;


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring()
                .requestMatchers("/docs/**", "/error", "/favicon.ico", "/v3/api-docs/**");
    }

    @Bean
    public SecurityFilterChain httpSecurity(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(customAuthenticationEntryPoint)
                        .accessDeniedHandler(customAccessDeniedHandler)
                )
                .sessionManagement(
                        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        // 모두 허용
                        .requestMatchers("/api/auth/**", "/api/tasty-restaurants").permitAll()
                        // User 권한만 허용
                        .requestMatchers("/api/auth/signout").hasRole("USER")
                        .requestMatchers("/api/auth/oauth/success").hasRole("USER")
                        .requestMatchers("/api/meetings/**").hasRole("USER")
                        // 그 외 요청은 인증 필요
                        .anyRequest().authenticated())
                .oauth2Login(oauth ->
                        oauth.userInfoEndpoint(userInfo ->
                                        userInfo.userService(customOAuth2UserService))
                                .successHandler(customOAuth2SuccessHandler)
                                .failureHandler(customOAuth2FailureHandler)
                )
                .addFilterBefore(jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
