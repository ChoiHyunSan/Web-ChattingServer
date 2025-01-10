package com.ll.webchattingserver.global.security.jwt.token;

import com.ll.webchattingserver.global.exception.clazz.security.InvalidTokenAccessException;
import com.ll.webchattingserver.global.security.jwt.authenticate.JwtKeyProvider;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.security.Key;

@Component
@RequiredArgsConstructor
public class TokenValidator {

    private final JwtKeyProvider keyProvider;
    private final TokenParser tokenParser;

    public boolean validateAccessToken(String token) {
        try {
            return parseToken(token, keyProvider.getAccessKey());
        } catch (JwtException e) {
            throw new InvalidTokenAccessException();
        }
    }

    public void validateRefreshToken(String token) {
        parseToken(token, keyProvider.getRefreshKey());
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
}
