package com.ll.webchattingserver.global.security.jwt.token;

import com.ll.webchattingserver.global.security.CustomUserDetails;
import com.ll.webchattingserver.global.security.jwt.authenticate.JwtKeyProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
@RequiredArgsConstructor
@Slf4j
public class TokenGenerator {

    @Value("${token.token-time}")
    private long tokenTimeForMinute;

    @Value("${token.refresh-token-time}")
    private long refreshTokenTimeForMinute;

    private final JwtKeyProvider keyProvider;

    public String createAccessToken(CustomUserDetails userDetails) {
        return createToken(
                userDetails.getUsername(),
                userDetails.getId(),
                userDetails.getRole(),
                keyProvider.getAccessKey(),
                tokenTimeForMinute
        );
    }

    public String createRefreshToken(CustomUserDetails userDetails) {
        return createToken(
                userDetails.getUsername(),
                userDetails.getId(),
                userDetails.getRole(),
                keyProvider.getRefreshKey(),
                refreshTokenTimeForMinute
        );
    }

    private String createToken(String username, Long userId, String role, Key key, long expireTime) {
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
}
