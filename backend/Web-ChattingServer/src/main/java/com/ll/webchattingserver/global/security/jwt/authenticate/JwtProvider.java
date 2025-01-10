package com.ll.webchattingserver.global.security.jwt.authenticate;

import com.ll.webchattingserver.global.security.CustomUserDetails;
import com.ll.webchattingserver.global.security.UserPrincipal;
import com.ll.webchattingserver.global.security.jwt.token.TokenExtractor;
import com.ll.webchattingserver.global.security.jwt.token.TokenGenerator;
import com.ll.webchattingserver.global.security.jwt.token.TokenParser;
import com.ll.webchattingserver.global.security.jwt.token.TokenValidator;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Collections;
import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtProvider {

    private final TokenGenerator tokenGenerator;
    private final TokenValidator tokenValidator;
    private final TokenExtractor tokenExtractor;
    private final TokenParser tokenParser;

    public String createAccessToken(CustomUserDetails userDetails) {
        return tokenGenerator.createAccessToken(userDetails);
    }

    public String createRefreshToken(CustomUserDetails userDetails) {
        return tokenGenerator.createRefreshToken(userDetails);
    }

    public boolean validateToken(String token) {
        return tokenValidator.validateAccessToken(token);
    }

    public void validateRefreshToken(String token) {
        tokenValidator.validateRefreshToken(token);
    }

    public String getUsernameByAccessToken(String token) {
        return tokenParser.getUsernameFromAccessToken(token);
    }

    public String getUsernameByRefreshToken(String token) {
        return tokenParser.getUsernameFromRefreshToken(token);
    }

    public Long getUserIdByAccessToken(String token) {
        return tokenParser.getUserIdFromAccessToken(token);
    }

    public Long getUserIdByRefreshToken(String token) {
        return tokenParser.getUserIdFromRefreshToken(token);
    }

    public String getRoleByRefreshToken(String token) {
        return tokenParser.getRoleFromRefreshToken(token);
    }

    public String extractTokenByHeader(String bearerToken) {
        return tokenExtractor.extractFromHeader(bearerToken);
    }

    public String extractTokenByHeader(HttpServletRequest request) {
        return tokenExtractor.extractFromRequest(request);
    }

    public Authentication getAuthentication(String token) {
        UserPrincipal principal = tokenParser.getUserPrincipal(token);
        return new UsernamePasswordAuthenticationToken(
                principal,
                null,
                Collections.emptyList()
        );
    }
}