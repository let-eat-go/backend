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
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(
                        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(authorize -> authorize
                        // static
                        .requestMatchers("/docs/**", "/error", "/favicon.ico",
                                "/v3/api-docs/**", "/ws").permitAll()

                        // api
                        .requestMatchers("/api/auth/**",
                                "/api/tasty-restaurants",
                                "/api/regions/**",
                                "/api/meetings/detail/**",
                                "/api/meetings/list",
                                "/api/meetings/search"
                        )

                        .permitAll()

                        // role
                        .requestMatchers("/api/auth/signout",
                                "/api/auth/oauth/success").hasRole("USER")
                        .requestMatchers("/api/meetings/**").hasRole("USER")
                        .requestMatchers("/api/notification/**").hasRole("USER")

                        .anyRequest().authenticated())

                .oauth2Login(oauth ->
                        oauth.userInfoEndpoint(userInfo ->
                                        userInfo.userService(customOAuth2UserService))
                                .successHandler(customOAuth2SuccessHandler)
                                .failureHandler(customOAuth2FailureHandler)
                )

                .addFilterBefore(jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class)

                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(customAuthenticationEntryPoint)
                        .accessDeniedHandler(customAccessDeniedHandler)
                )
                .build();
    }
}
