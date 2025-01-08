package com.ll.webchattingserver.global.jwt;

import com.ll.webchattingserver.domain.user.User;
import com.ll.webchattingserver.global.security.UserPrincipal;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.lettuce.core.resource.ClientResources;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtProvider {
    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    
    private final long accessTokenValidityInMilliseconds = 1000L * 60 * 60; // 1시간

    public String getUsername(String token) {
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

    public String createToken(User user, String role) {
        Claims claims = Jwts.claims().setSubject(user.getUsername());
        claims.put("username", user.getUsername());
        claims.put("id", user.getId());

        Date now = new Date();
        Date validity = new Date(now.getTime() + accessTokenValidityInMilliseconds);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(key)
                .compact();
    }

    public boolean validateToken(String token) {
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

    public JwtProvider(ClientResources clientResources) {
        this.clientResources = clientResources;
    }

    private final ClientResources clientResources;

    public UserPrincipal getUserPrincipal(String token) {
        Claims claims = getClaims(token, key);
        return new UserPrincipal(claims.get("id", Long.class), claims.get("username", String.class));
    }
}