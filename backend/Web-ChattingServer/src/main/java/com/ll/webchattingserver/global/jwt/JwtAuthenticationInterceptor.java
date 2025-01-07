package com.ll.webchattingserver.global.jwt;

import com.ll.webchattingserver.domain.user.UserService;
import com.ll.webchattingserver.global.security.JwtProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class JwtAuthenticationInterceptor implements ChannelInterceptor {

    private final JwtProvider jwtProvider;
    private final UserService userService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        switch (accessor.getCommand()) {
            case CONNECT -> {
                String token = extractToken((HttpServletRequest) accessor);
                if (token != null && jwtProvider.validateToken(token)) {
                    String username = jwtProvider.getUsername(token);
                    if(userService.checkInvalidUser(username)){
                        accessor.setUser(() -> username);
                    }
                } else {
                    throw new MessageDeliveryException("Invalid token");
                }
            }
            case SUBSCRIBE -> {
                if (accessor.getUser() == null) {
                    throw new MessageDeliveryException("Not authenticated");
                }
                // 추가로 채팅방 구독 권한 검증 가능

            }
            case SEND -> {
                // 메시지 전송 시 권한 검증
                if (accessor.getUser() == null) {
                    throw new MessageDeliveryException("Not authenticated");
                }
                // 추가로 메시지 전송 권한이나 내용 검증 가능

            }
        }
        return message;
    }

    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
