package com.ll.webchattingserver.socket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class WebSocketRoomManager {
    private final Map<String, Set<String>> userRooms = new ConcurrentHashMap<>();

    public void addUserToRoom(String username, String roomId) {
        userRooms.computeIfAbsent(username, k -> ConcurrentHashMap.newKeySet()).add(roomId);
        log.info("User {} subscribed to room {}", username, roomId);
    }

    public void removeUserFromRoom(String username, String roomId) {
        userRooms.getOrDefault(username, Collections.emptySet()).remove(roomId);
        log.info("User {} unsubscribed from room {}", username, roomId);
    }

    public void removeUser(String username) {
        userRooms.remove(username);
        log.info("User {} disconnected", username);
    }

    public boolean isUserInRoom(String username, String roomId) {
        return userRooms.getOrDefault(username, Collections.emptySet()).contains(roomId);
    }
}
