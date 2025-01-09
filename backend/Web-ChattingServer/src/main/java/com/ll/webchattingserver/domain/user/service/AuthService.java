package com.ll.webchattingserver.domain.user.service;

import com.ll.webchattingserver.domain.user.dto.request.LoginRequest;
import com.ll.webchattingserver.domain.user.dto.response.TokenResponse;
import com.ll.webchattingserver.global.security.CustomUserDetails;
import com.ll.webchattingserver.global.security.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
        String newAccessToken = jwtProvider.createAccessToken(userDetails);
        String newRefreshToken = jwtProvider.createRefreshToken(userDetails);

        return TokenResponse.of(
                newAccessToken,
                newRefreshToken,
                request.getUsername());
    }

    public TokenResponse refresh(String refreshToken) {
        String extractToken = jwtProvider.extractToken(refreshToken);
        jwtProvider.validateRefreshToken(extractToken);

        String username = jwtProvider.getUsernameByRefreshToken(extractToken);
        Long userId = jwtProvider.getUserIdByRefreshToken(extractToken);
        String role = jwtProvider.getRoleByRefreshToken(extractToken);
        log.info("Username: {}, UserId: {} Role: {}", username, userId, role);

        String newAccessToken = jwtProvider.createAccessToken(username, userId, role);
        String newRefreshToken = jwtProvider.createRefreshToken(username, userId, role);

        // SecurityContext 갱신
        Authentication authentication = jwtProvider.getAuthentication(newAccessToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        log.info("Refresh! New Access Token: {}, New Refresh Token: {}", newAccessToken, newRefreshToken);

        return TokenResponse.of(
                newAccessToken,
                newRefreshToken,
                username);
    }
}
