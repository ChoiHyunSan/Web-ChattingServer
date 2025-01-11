package com.ll.webchattingserver.core.domain.auth.service;

import com.ll.webchattingserver.core.domain.auth.dto.request.LoginRequest;
import com.ll.webchattingserver.core.domain.auth.dto.response.TokenResponse;
import com.ll.webchattingserver.global.security.CustomUserDetails;
import com.ll.webchattingserver.global.security.jwt.authenticate.JwtProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtProvider jwtProvider;
    private final AuthenticationManager authenticationManager;

    public TokenResponse login(LoginRequest request) {
        Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        CustomUserDetails userDetails = (CustomUserDetails) authenticate.getPrincipal();
        return createTokenResponse(userDetails);
    }

    public TokenResponse refresh(HttpServletRequest request) {
        String extractToken = jwtProvider.extractTokenByHeader(request);
        CustomUserDetails details = jwtProvider.extractUserDetailsFromRefreshToken(extractToken);

        TokenResponse response = createTokenResponse(details);

        log.info("Refresh! New Access Token: {}, New Refresh Token: {}",
                response.getToken(),
                response.getRefreshToken());

        return response;
    }

    private TokenResponse createTokenResponse(CustomUserDetails userDetails) {
        String newAccessToken = jwtProvider.createAccessToken(userDetails);
        String newRefreshToken = jwtProvider.createRefreshToken(userDetails);

        return TokenResponse.of(
                newAccessToken,
                newRefreshToken,
                userDetails.getUsername());
    }
}
