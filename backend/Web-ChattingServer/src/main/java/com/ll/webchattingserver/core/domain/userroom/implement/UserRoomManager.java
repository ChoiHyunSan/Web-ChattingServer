package com.ll.webchattingserver.core.domain.userroom.implement;

import com.ll.webchattingserver.entity.room.Room;
import com.ll.webchattingserver.entity.user.User;
import com.ll.webchattingserver.entity.userroom.UserRoom;
import com.ll.webchattingserver.entity.userroom.repository.UserRoomQueryRepository;
import com.ll.webchattingserver.entity.userroom.repository.UserRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserRoomManager {

    private final UserRoomRepository userRoomRepository;
    private final UserRoomQueryRepository userRoomQueryRepository;

    public void createUserRoom(User user, Room room) {
        UserRoom userRoom = UserRoom.of(user, room);
        user.getUserRooms().add(userRoom);
        room.getUserRooms().add(userRoom);
        userRoomRepository.save(userRoom);
    }

    public void deleteUserRoom(UUID roomId, Long userId) {
        userRoomQueryRepository.deleteByRoomIdAndUserId(roomId, userId);
    }
}
