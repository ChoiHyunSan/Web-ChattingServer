package com.ll.webchattingserver.global.jwt;

import com.ll.webchattingserver.domain.user.UserService;
import com.ll.webchattingserver.global.security.JwtProvider;
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

        switch (accessor.getCommand()) {
            case CONNECT -> {
                String token = extractToken(accessor);
                if (token != null && jwtProvider.validateToken(token)) {
                    String username = jwtProvider.getUsername(token);
                    if(userService.checkInvalidUser(username)){
                        accessor.setUser(() -> username);
                    }
                    userRooms.putIfAbsent(username, ConcurrentHashMap.newKeySet());
                    log.info("User connected and initialized: {}", username);
                } else {
                    throw new MessageDeliveryException("Invalid token");
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

    private String extractToken(StompHeaderAccessor accessor) {
        String bearerToken = accessor.getFirstNativeHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
