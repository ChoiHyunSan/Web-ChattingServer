package com.ll.webchattingserver.global.security.jwt;

import com.ll.webchattingserver.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationInterceptor implements ChannelInterceptor {

    private final JwtProvider jwtProvider;
    private final UserService userService;

    private Map<String, Set<String>> userRooms = new ConcurrentHashMap<>();

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        log.info("Command: {}", accessor.getCommand()); // 로그 추가
        switch (accessor.getCommand()) {
            case CONNECT -> {
                try {
                    String bearerToken = accessor.getFirstNativeHeader("Authorization");
                    String token = jwtProvider.extractToken(bearerToken);
                    log.info("Extracted token: {}", token); // 토큰 로그

                    if (token == null) {
                        log.error("Token is null");
                        throw new MessageDeliveryException("Token is required");
                    }

                    // 토큰 유효성 검사
                    if (!jwtProvider.validateToken(token)) {
                        log.error("Invalid token");
                        throw new MessageDeliveryException("Invalid token");
                    }

                    String username = jwtProvider.getUsernameByAccessToken(token);
                    if (username == null) {
                        log.error("Username not found in token");
                        throw new MessageDeliveryException("Username not found in token");
                    }

                    accessor.setUser(() -> username);
                    userRooms.putIfAbsent(username, ConcurrentHashMap.newKeySet());
                    log.info("User connected successfully: {}", username);
                } catch (Exception e) {
                    log.error("WebSocket connection error: ", e);
                    throw new MessageDeliveryException("Authentication failed: " + e.getMessage());
                }
            }
            case SUBSCRIBE -> {
                if (accessor.getUser() == null) {
                    throw new MessageDeliveryException("Not authenticated");
                }
                String username = accessor.getUser().getName();
                String roomId = extractRoomId(accessor.getDestination());
                userRooms.get(username).add(roomId);
                log.info("User {} subscribed to room {}", username, roomId);
            }
            case UNSUBSCRIBE -> {
                String username = accessor.getUser().getName();
                String roomId = extractRoomId(accessor.getDestination());
                userRooms.get(username).remove(roomId);
                log.info("User {} unsubscribed from room {}", username, roomId);
            }
            case SEND -> {
                String username = accessor.getUser().getName();
                if (accessor.getUser() == null) {
                    throw new MessageDeliveryException("Not authenticated");
                }
                String roomId = extractRoomId(accessor.getDestination());

                // 인메모리에서 빠르게 확인
                if (!userRooms.getOrDefault(username, Collections.emptySet()).contains(roomId)) {
                    throw new MessageDeliveryException("Not authorized for this room");
                }
            }
            case DISCONNECT -> {
                if (accessor.getUser() != null) {
                    userRooms.remove(accessor.getUser().getName());
                    log.info("User Disconnected");
                }
            }
        }
        return message;
    }

    private String extractRoomId(String destination) {
        if (destination == null) {
            throw new IllegalArgumentException("Destination cannot be null");
        }

        String[] parts = destination.split("/");

        // SUBSCRIBE: /sub/chat/room/{roomId}
        if (destination.startsWith("/sub/chat/room/")) {
            return parts[4];
        }

        // SEND: /chat/message/{roomId}
        if (destination.startsWith("/chat/message/")) {
            return parts[3];
        }

        // PUB: /pub/chat/message/{roomId}
        if (destination.startsWith("/pub/chat/message/")) {
            return parts[4];
        }

        throw new IllegalArgumentException("Invalid destination pattern: " + destination);
    }
}
