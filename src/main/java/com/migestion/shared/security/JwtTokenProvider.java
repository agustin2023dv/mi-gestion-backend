package com.migestion.shared.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {

    private static final Duration ACCESS_TOKEN_TTL = Duration.ofMinutes(15);
    private static final Duration REFRESH_TOKEN_TTL = Duration.ofDays(7);
    private static final String TENANT_ID_CLAIM = "tenant_id";
    private static final String ROLE_CLAIM = "role";
    private static final String PERMISSIONS_CLAIM = "permissions";
    private static final String BLACKLIST_KEY_PREFIX = "security:jwt:blacklist:";

    private final StringRedisTemplate stringRedisTemplate;
    private final SecretKey signingKey;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String jwtSecret,
            StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.signingKey = buildSigningKey(jwtSecret);
    }

    public String generateAccessToken(
            Long userId,
            Long tenantId,
            String role,
            Collection<String> permissions) {
        return generateToken(userId, tenantId, role, permissions, ACCESS_TOKEN_TTL);
    }

    public String generateRefreshToken(
            Long userId,
            Long tenantId,
            String role,
            Collection<String> permissions) {
        return generateToken(userId, tenantId, role, permissions, REFRESH_TOKEN_TTL);
    }

    public boolean validateToken(String token) {
        try {
            Claims claims = extractClaims(token);
            String jti = claims.getId();
            return jti == null || !isTokenInvalidated(jti);
        } catch (JwtException | IllegalArgumentException exception) {
            return false;
        }
    }

    public Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public void invalidateToken(String token) {
        Claims claims;
        try {
            claims = extractClaims(token);
        } catch (ExpiredJwtException expiredJwtException) {
            claims = expiredJwtException.getClaims();
        }

        if (claims == null || claims.getId() == null || claims.getExpiration() == null) {
            return;
        }

        Instant now = Instant.now();
        Instant expiration = claims.getExpiration().toInstant();
        if (expiration.isBefore(now) || expiration.equals(now)) {
            return;
        }

        Duration ttl = Duration.between(now, expiration);
        stringRedisTemplate.opsForValue().set(blacklistKey(claims.getId()), "invalid", ttl);
    }

    private String generateToken(
            Long userId,
            Long tenantId,
            String role,
            Collection<String> permissions,
            Duration ttl) {
        String normalizedRole = Objects.requireNonNull(role, "role must not be null").trim();
        if (normalizedRole.isEmpty()) {
            throw new IllegalArgumentException("role must not be blank");
        }

        Instant now = Instant.now();
        Instant expiration = now.plus(ttl.toSeconds(), ChronoUnit.SECONDS);
        String jti = UUID.randomUUID().toString();

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .id(jti)
                .claim(TENANT_ID_CLAIM, tenantId)
            .claim(ROLE_CLAIM, normalizedRole)
                .claim(PERMISSIONS_CLAIM, permissions)
                .claim(Claims.ID, jti)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiration))
                .signWith(signingKey)
                .compact();
    }

    private boolean isTokenInvalidated(String jti) {
        Boolean hasKey = stringRedisTemplate.hasKey(blacklistKey(jti));
        return Boolean.TRUE.equals(hasKey);
    }

    private String blacklistKey(String jti) {
        return BLACKLIST_KEY_PREFIX + jti;
    }

    private SecretKey buildSigningKey(String jwtSecret) {
        byte[] keyBytes;
        try {
            keyBytes = Decoders.BASE64.decode(jwtSecret);
        } catch (IllegalArgumentException exception) {
            keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        }

        return Keys.hmacShaKeyFor(keyBytes);
    }
}
