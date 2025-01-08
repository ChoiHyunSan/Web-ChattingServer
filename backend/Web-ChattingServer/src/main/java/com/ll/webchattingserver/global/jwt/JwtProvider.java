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

    private Claims getClaims(String token, Key key) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String createToken(User user, String role) {
        Claims claims = Jwts.claims().setSubject(user.getUsername());
        claims.put("username", user.getUsername());
        claims.put("id", user.getId());

        Date now = new Date();
        Date validity = new Date(now.getTime() + tokenTimeForMinute);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(secretKey)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
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
}