package com.ll.webchattingserver.global.security.jwt;

import com.ll.webchattingserver.domain.user.User;
import com.ll.webchattingserver.global.exception.clazz.security.InvalidTokenAccessException;
import com.ll.webchattingserver.global.security.CustomUserDetails;
import com.ll.webchattingserver.global.security.UserPrincipal;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.lettuce.core.resource.ClientResources;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Collections;
import java.util.Date;

@Slf4j
@Component
public class JwtProvider {

    @Value("${token.secret-key}")
    private String secretKeyStr;

    @Value("${token.refresh-secret-key}")
    private String refreshSecretKeyStr;

    @Value("${token.token-time}")
    private long tokenTimeForMinute;

    @Value("${token.refresh-token-time}")
    private long refreshTokenTimeForMinute;

    private Key secretKey;
    private Key refreshSecretKey;

    @PostConstruct
    public void init() {
        this.secretKey = Keys.hmacShaKeyFor(secretKeyStr.getBytes());
        this.refreshSecretKey = Keys.hmacShaKeyFor(refreshSecretKeyStr.getBytes());
    }

    public String getUsernameByAccessToken(String token) {
        return getUsername(token, secretKey);
    }

    public String getUsernameByRefreshToken(String token) {
        return getUsername(token, refreshSecretKey);
    }

    public String getRoleByRefreshToken(String token) {
        return getRole(token, refreshSecretKey);
    }

    public Long getUserIdByAccessToken(String token) {
        return getUserId(token, secretKey);
    }

    public Long getUserIdByRefreshToken(String token) {
        return getUserId(token, refreshSecretKey);
    }

    private Long getUserId(String token, Key key) {
        try {
            Claims claims = getClaims(token, key);
            return claims.get("id", Long.class);
        } catch (JwtException | IllegalArgumentException e) {
            return null;
        }
    }

    private String getRole(String token, Key key) {
        try {
            Claims claims = getClaims(token, key);
            return claims.get("role", String.class);
        } catch (JwtException | IllegalArgumentException e) {
            return null;
        }
    }

    private String getUsername(String token, Key key){
        try {
            Claims claims = getClaims(token, key);
            return claims.get("username", String.class);
        } catch (JwtException | IllegalArgumentException e) {
            return null;
        }
    }

    private Claims getClaims(String token, Key key) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String createRefreshToken(CustomUserDetails userDetails) {
        return createToken(
                userDetails.getUsername(),
                userDetails.getId(),
                userDetails.getRole(),
                refreshSecretKey,
                refreshTokenTimeForMinute);
    }

    public String createAccessToken(CustomUserDetails userDetails) {
        return createToken(
                userDetails.getUsername(),
                userDetails.getId(),
                userDetails.getRole(),
                secretKey,
                tokenTimeForMinute);
    }

    public String createRefreshToken(String username, Long id, String role) {
        return createToken(
                username,
                id,
                role,
                refreshSecretKey,
                refreshTokenTimeForMinute);
    }

    public String createAccessToken(String username, Long id, String role) {
        return createToken(
                username,
                id,
                role,
                secretKey,
                tokenTimeForMinute);
    }

    public String createToken(String username, Long userId, String role, Key key, long expireTime) {
        Claims claims = Jwts.claims();
        claims.put("username", username);
        claims.put("id", userId);
        claims.put("role", role);

        Date now = new Date();
        Date validity = new Date(now.getTime() + expireTime);

        log.info("Create Token! Username: {}, Id: {}, Role: {}, Date: {}", username, userId, role, validity);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(key)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            return parseToken(token, secretKey);
        } catch (JwtException e) {
            throw new InvalidTokenAccessException();
        }
    }

    public void validateRefreshToken(String token) {
        parseToken(token, refreshSecretKey);
    }

    private boolean parseToken(String token, Key key) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            throw new InvalidTokenAccessException();
        }
    }

    private final ClientResources clientResources;

    public JwtProvider(ClientResources clientResources) {
        this.clientResources = clientResources;
    }

    public UserPrincipal getUserPrincipal(String token) {
        Claims claims = getClaims(token, secretKey);
        return new UserPrincipal(
                claims.get("id", Long.class),
                claims.get("username", String.class)
        );
    }

    public String extractToken(String bearerToken) {
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public Authentication getAuthentication(String token) {
        UserPrincipal principal = getUserPrincipal(token);
        return new UsernamePasswordAuthenticationToken(
                principal,
                null,
                Collections.emptyList()
        );
    }
}