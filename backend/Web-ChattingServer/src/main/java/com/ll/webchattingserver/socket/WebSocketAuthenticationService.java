package com.ll.webchattingserver.socket;

import com.ll.webchattingserver.global.security.jwt.authenticate.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebSocketAuthenticationService {
    private final JwtProvider jwtProvider;

    public String authenticateConnection(String bearerToken) {
        String token = jwtProvider.extractTokenByHeader(bearerToken);
        log.info("Extracted token: {}", token);

        if (token == null) {
            throw new MessageDeliveryException("Token is required");
        }

        if (!jwtProvider.validateToken(token)) {
            throw new MessageDeliveryException("Invalid token");
        }

        String username = jwtProvider.getUsernameByAccessToken(token);
        if (username == null) {
            throw new MessageDeliveryException("Username not found in token");
        }

        return username;
    }

    public static void validateAuthentication(StompHeaderAccessor accessor) {
        if (accessor.getUser() == null) {
            throw new MessageDeliveryException("Not authenticated");
        }
    }
}
