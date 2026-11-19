package com.heybro.heybro.common.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

@Slf4j
@Component
public class JwtUtil {

    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String TOKEN_TYPE_CLAIM = "typ";
    public static final String TOKEN_TYPE_ACCESS = "access";
    public static final String TOKEN_TYPE_REFRESH = "refresh";

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration.access-token}")
    private long accessTokenExpiration;

    @Value("${jwt.expiration.refresh-token}")
    private long refreshTokenExpiration;

    private SecretKey key;

    @PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(bytes);
    }

    // Access Token 생성
    public String createAccessToken(String email) {
        return generateToken(email, accessTokenExpiration, TOKEN_TYPE_ACCESS);
    }

    // Refresh Token 생성
    public String createRefreshToken(String email) {
        return generateToken(email, refreshTokenExpiration, TOKEN_TYPE_REFRESH);
    }

    // typ 클레임으로 access/refresh 를 구분해, 수명이 긴 refresh 토큰이 API 인증에 쓰이는 것을 막는다
    private String generateToken(String email, long expiration, String tokenType) {
        Date now = new Date();
        JwtBuilder builder = Jwts.builder()
                .subject(email)
                .claim(TOKEN_TYPE_CLAIM, tokenType)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expiration))
                .signWith(key);

        return builder.compact();
    }

    // access 토큰인지 확인. typ 클레임이 없는 토큰은 이 클레임을 도입하기 전에 발급된 것이므로 access 로 간주한다
    // (refresh 토큰 만료 기간인 7일이 지나면 typ 없는 토큰은 더 이상 유효하지 않다)
    public boolean isAccessToken(String token) {
        String type = getClaims(token).get(TOKEN_TYPE_CLAIM, String.class);
        return type == null || TOKEN_TYPE_ACCESS.equals(type);
    }

    public boolean isRefreshToken(String token) {
        String type = getClaims(token).get(TOKEN_TYPE_CLAIM, String.class);
        return type == null || TOKEN_TYPE_REFRESH.equals(type);
    }

    // 토큰에서 사용자 정보 가져오기
    public String getEmailFromToken(String token) {
        return getClaims(token).getSubject();
    }

    private Claims getClaims(String token) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
    }

    // 토큰에서 만료 시간 추출하여 남은 유효 시간 계산하기
    public long getRemainingTime(String token) {
        try {
            Claims claims = getClaims(token);
            Date expirationDate = claims.getExpiration();
            long now = new Date().getTime();
            return expirationDate.getTime() - now;
        } catch (Exception e) {
            log.error("유효하지 않은 토큰입니다: {}", e.getMessage());
            return 0;
        }
    }

    // 토큰 유효성 검증
    public boolean validateToken(String token) {
        try {
            getClaims(token);
            return true;
        } catch (SecurityException | MalformedJwtException | SignatureException e) {
            log.error("Invalid JWT signature, 유효하지 않는 JWT 서명 입니다. Error: {}", e.getMessage());
            throw new JwtException("Invalid JWT signature");
        } catch (ExpiredJwtException e) {
            log.error("Expired JWT token, 만료된 JWT token 입니다. Error: {}", e.getMessage());
            throw e;
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다. Error: {}", e.getMessage());
            throw new JwtException("Unsupported JWT token");
        } catch (IllegalArgumentException e) {
            log.error("JWT claims is empty, 잘못된 JWT 토큰 입니다. Error: {}", e.getMessage());
            throw new JwtException("JWT claims is empty");
        }
    }
}
