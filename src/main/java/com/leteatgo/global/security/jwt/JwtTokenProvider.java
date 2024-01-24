package com.leteatgo.global.security.jwt;

import static com.leteatgo.global.exception.ErrorCode.EMPTY_TOKEN;
import static com.leteatgo.global.exception.ErrorCode.EXPIRED_TOKEN;
import static com.leteatgo.global.exception.ErrorCode.INVALID_TOKEN;
import static java.nio.charset.StandardCharsets.UTF_8;

import com.leteatgo.domain.auth.entity.RedisToken;
import com.leteatgo.domain.auth.exception.TokenException;
import com.leteatgo.domain.auth.service.TokenService;
import com.leteatgo.global.security.CustomUserDetailService;
import com.leteatgo.global.security.CustomUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.time.Duration;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.crypto.SecretKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@Slf4j
public class JwtTokenProvider {

    private static final long ACCESS_TOKEN_EXPIRE_TIME = Duration.ofMinutes(30).toMillis();
    private static final long REFRESH_TOKEN_EXPIRE_TIME = Duration.ofDays(7).toMillis();
    private static final String KEY_ROLE = "role";


    private final SecretKey key;
    private final String issuer;
    private final TokenService tokenService;
    private final CustomUserDetailService customUserDetailsService;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String key,
            @Value("${jwt.issuer}") String issuer,
            TokenService tokenService,
            CustomUserDetailService customUserDetailsService
    ) {
        this.key = Keys.hmacShaKeyFor(key.getBytes(UTF_8));
        this.issuer = issuer;
        this.tokenService = tokenService;
        this.customUserDetailsService = customUserDetailsService;
    }

    public String createAccessToken(Authentication authentication) {
        return createToken(authentication, ACCESS_TOKEN_EXPIRE_TIME);
    }

    public String createRefreshToken(Authentication authentication) {
        return createToken(authentication, REFRESH_TOKEN_EXPIRE_TIME);
    }

    private String createToken(Authentication authentication, long expireTime) {
        Date now = new Date();
        Date expiredDate = new Date(now.getTime() + expireTime);

        Claims claims = Jwts.claims().setSubject(authentication.getName());
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        claims.put(KEY_ROLE, authorities);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiredDate)
                .setIssuer(issuer)
                .signWith(key)
                .compact();
    }

    public Authentication getAuthentication(String token) {
        Claims claims = parseClaims(token);
        List<SimpleGrantedAuthority> authorities = getAuthorities(claims);

        CustomUserDetails userDetails = customUserDetailsService.loadUserById(
                Long.valueOf(claims.getSubject()));
        return new UsernamePasswordAuthenticationToken(userDetails, token, authorities);
    }

    private List<SimpleGrantedAuthority> getAuthorities(Claims claims) {
        return Collections.singletonList(
                new SimpleGrantedAuthority(claims.get(KEY_ROLE).toString()));
    }

    public String reissueAccessToken(String accessToken) {
        RedisToken token = tokenService.getTokenByAccessToken(accessToken);
        String refreshToken = token.getRefreshToken();

        if (!validateToken(refreshToken)) {
            throw new TokenException(EXPIRED_TOKEN);
        }

        String reissuedAccessToken = createAccessToken(getAuthentication(refreshToken));
        tokenService.updateToken(token, reissuedAccessToken);
        return reissuedAccessToken;
    }

    public boolean validateToken(String token) {
        if (!StringUtils.hasText(token)) {
            throw new TokenException(EMPTY_TOKEN);
        }

        Claims claims = parseClaims(token);
        log.info("claims : {}", claims);
        return claims.getExpiration().after(new Date());
    }

    private Claims parseClaims(String token) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        } catch (JwtException | IllegalArgumentException e) {
            throw new TokenException(INVALID_TOKEN);
        }
    }
}
