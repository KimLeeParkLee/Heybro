package com.heybro.heybro.common.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey; // Import this
import java.util.Base64;
import java.util.Date;

@Slf4j
@Component
public class JwtUtil {

    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration.access-token}")
    private long accessTokenExpiration;

    @Value("${jwt.expiration.refresh-token}")
    private long refreshTokenExpiration;

    private SecretKey key; // Change type to SecretKey

    @PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(bytes);
    }

    // Access Token 생성
    public String generateAccessToken(String email) {
        Date now = new Date();
        return Jwts.builder()
                        .subject(email)
                        .issuedAt(now)
                        .expiration(new Date(now.getTime() + accessTokenExpiration))
                        .signWith(key)
                        .compact();
    }

    // Refresh Token 생성
    public String generateRefreshToken(String email) {
        Date now = new Date();
        return Jwts.builder()
                .subject(email)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + refreshTokenExpiration))
                .signWith(key)
                .compact();
    }

    // 토큰에서 사용자 정보 가져오기
    public String getEmailFromToken(String token) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().getSubject();
    }

    // 토큰에서 만료 시간 추출하여 남은 유효 시간 계산하기
    public long getRemainingTime(String token) {
        try {
            // 토큰 파싱해서 클레임 가져오기
            Claims claims = Jwts.parser()
                    .verifyWith(key) // key는 JwtUtil에 이미 있는 서명 키 변수
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            Date expirationDate = claims.getExpiration();
            long now = new Date().getTime();

            // 남은 유효 시간 계산
            return expirationDate.getTime() - now;
        } catch (Exception e) {
            // 토큰이 유효하지 않거나 만료된 경우 0을 반환
            log.error("유효하지 않은 토큰입니다: {}", e.getMessage());
            return 0;
        }
    }

    // 토큰 유효성 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.error("Invalid JWT signature, 유효하지 않는 JWT 서명 입니다. Error: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("Expired JWT token, 만료된 JWT token 입니다. Error: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다. Error: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims is empty, 잘못된 JWT 토큰 입니다. Error: {}", e.getMessage());
        }
        return false;
    }
}
