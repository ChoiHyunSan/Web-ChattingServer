package com.ll.webchattingserver.socket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class WssAccessorManager {

    private final WebSocketAuthenticationService webSocketAuthenticationService;
    private final WebSocketDestinationParser webSocketDestinationParser;
    private final WebSocketRoomManager webSocketRoomManager;

    public void checkMessageCommand(Message<?> message){
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        log.info("Command: {}", accessor.getCommand());
        if(accessor.getCommand() == StompCommand.CONNECT){
            try {
                String bearerToken = accessor.getFirstNativeHeader("Authorization");
                String username = webSocketAuthenticationService.authenticateConnection(bearerToken);
                accessor.setUser(() -> username);
            } catch (Exception e) {
                log.error("WebSocket connection error: ", e);
                throw new MessageDeliveryException("Authentication failed: " + e.getMessage());
            }
            return;
        }

        webSocketAuthenticationService.validateAuthentication(accessor);
        String username = Objects.requireNonNull(accessor.getUser()).getName();
        String roomId = webSocketDestinationParser.extractRoomId(accessor.getDestination());

        switch (Objects.requireNonNull(accessor.getCommand())) {
            case SUBSCRIBE -> {
                webSocketRoomManager.addUserToRoom(username, roomId);
            }
            case UNSUBSCRIBE -> {
                webSocketRoomManager.removeUserFromRoom(username, roomId);
            }
            case SEND -> {
                if (!webSocketRoomManager.isUserInRoom(username, roomId)) {
                    throw new MessageDeliveryException("Not authorized for this room");
                }
            }
            case DISCONNECT -> {
                webSocketRoomManager.removeUserFromRoom(accessor.getUser().getName(), accessor.getDestination());
            }
        }
    }
}
