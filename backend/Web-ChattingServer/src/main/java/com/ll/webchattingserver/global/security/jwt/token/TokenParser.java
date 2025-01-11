package com.ll.webchattingserver.global.security.jwt.token;

import com.ll.webchattingserver.global.security.CustomUserDetails;
import com.ll.webchattingserver.global.security.UserPrincipal;
import com.ll.webchattingserver.global.security.jwt.authenticate.JwtKeyProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.security.Key;

@Component
@RequiredArgsConstructor
public class TokenParser {

    private final JwtKeyProvider keyProvider;

    public String getUsernameFromAccessToken(String token) {
        return getUsername(token, keyProvider.getAccessKey());
    }

    public String getUsernameFromRefreshToken(String token) {
        return getUsername(token, keyProvider.getRefreshKey());
    }

    public Long getUserIdFromAccessToken(String token) {
        return getUserId(token, keyProvider.getAccessKey());
    }

    public Long getUserIdFromRefreshToken(String token) {
        return getUserId(token, keyProvider.getRefreshKey());
    }

    public String getRoleFromRefreshToken(String token) {
        return getRole(token, keyProvider.getRefreshKey());
    }

    public UserPrincipal getUserPrincipal(String token) {
        Claims claims = getClaims(token, keyProvider.getAccessKey());
        return new UserPrincipal(
                claims.get("id", Long.class),
                claims.get("username", String.class),
                claims.get("role", String.class)
        );
    }

    private Claims getClaims(String token, Key key) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private String getUsername(String token, Key key) {
        try {
            Claims claims = getClaims(token, key);
            return claims.get("username", String.class);
        } catch (JwtException | IllegalArgumentException e) {
            return null;
        }
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

    public CustomUserDetails getUserDetailsFromRefreshToken(String token) {
        try {
            Claims claims = getClaims(token, keyProvider.getRefreshKey());
            String username = claims.get("username", String.class);
            Long userId = claims.get("id", Long.class);
            String role = claims.get("role", String.class);

            return CustomUserDetails.builder()
                    .username(username)
                    .id(userId)
                    .role(role)
                    .build();
        } catch (JwtException | IllegalArgumentException e) {
            return null;
        }
    }
}
