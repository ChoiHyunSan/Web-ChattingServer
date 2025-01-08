package com.ll.webchattingserver.global.jwt;

import com.ll.webchattingserver.domain.user.User;
import com.ll.webchattingserver.global.security.UserPrincipal;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.lettuce.core.resource.ClientResources;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

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

    public String getUsername(String token) {
        try {
            Claims claims = getClaims(token, secretKey);
            return claims.get("username", String.class);
        } catch (JwtException | IllegalArgumentException e) {
            return null;
        }
    }

    public Long getUserId(String token) {
        try {
            Claims claims = getClaims(token, secretKey);
            return claims.get("id", Long.class);
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

    public String createRefreshToken(User user) {
        return createToken(user.getUsername(), user.getId(), refreshSecretKey, refreshTokenTimeForMinute);
    }

    public String createRefreshToken(String username, Long userId) {
        return createToken(username, userId, refreshSecretKey, refreshTokenTimeForMinute);
    }

    public String createAccessToken(User user) {
        return createToken(user.getUsername(), user.getId(), secretKey, tokenTimeForMinute);
    }

    public String createAccessToken(String username, Long userId) {
        return createToken(username, userId, secretKey, tokenTimeForMinute);
    }

    public String createToken(String username, Long userId, Key key, long expireTime) {
        Claims claims = Jwts.claims().setSubject(username);
        claims.put("username", username);
        claims.put("id", userId);

        Date now = new Date();
        Date validity = new Date(now.getTime() + expireTime);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(key)
                .compact();
    }

    public boolean validateToken(String token) {
        return parseToken(token, secretKey);
    }

    public boolean parseToken(String token, Key key) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private final ClientResources clientResources;

    public JwtProvider(ClientResources clientResources) {
        this.clientResources = clientResources;
    }

    public UserPrincipal getUserPrincipal(String token) {
        Claims claims = getClaims(token, secretKey);
        return new UserPrincipal(claims.get("id", Long.class), claims.get("username", String.class));
    }

    public String extractToken(String bearerToken) {
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}