package com.ll.webchattingserver.socket;

import org.springframework.stereotype.Component;

@Component
public class WebSocketDestinationParser {
    public String extractRoomId(String destination) {
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
