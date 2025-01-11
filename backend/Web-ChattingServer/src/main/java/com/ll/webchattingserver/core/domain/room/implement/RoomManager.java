package com.ll.webchattingserver.core.domain.room.implement;

import com.ll.webchattingserver.core.domain.userroom.implement.UserRoomManager;
import com.ll.webchattingserver.core.domain.userroom.implement.UserRoomReader;
import com.ll.webchattingserver.entity.room.Room;
import com.ll.webchattingserver.entity.room.repository.RoomRepository;
import com.ll.webchattingserver.entity.userroom.UserRoom;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RoomManager {

    private final RoomRepository roomRepository;
    private final UserRoomManager userRoomManager;

    private final RoomReader roomReader;
    private final UserRoomReader userRoomReader;

    public void delete(Room room) {
        roomRepository.delete(room);
    }

    public Room createRoom(String roomName) {
        Room room = Room.of(roomName);
        return roomRepository.save(room);
    }

    public void joinRoom(Long userId, UUID roomId) {
        userRoomManager.createUserRoom(userId, roomId);
    }

    public void leaveRoom(Long userId, UUID roomId) {
        Room room = roomReader.findRoom(roomId);

        List<UserRoom> userRooms = userRoomReader.findByRoom(room);
        userRoomManager.deleteUserRoom(roomId, userId);

        // 방이 사라지는 경우 (= 자기 자신 밖에 방에 없는 경우)
        if (userRooms.size() == 1) {
            delete(room);
        }
    }
}
