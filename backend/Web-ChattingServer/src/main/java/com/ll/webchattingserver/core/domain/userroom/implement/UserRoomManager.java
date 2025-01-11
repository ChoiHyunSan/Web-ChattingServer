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
    private final UserRoomReader userRoomReader;

    public void createUserRoom(Long userId, UUID roomId) {
        if(userRoomReader.findByUsernameAndRoomId(userId, roomId).isEmpty()){
            UserRoom userRoom = UserRoom.of(userId, roomId);
            userRoomRepository.save(userRoom);
        }
    }

    public void deleteUserRoom(UUID roomId, Long userId) {
        userRoomQueryRepository.deleteByRoomIdAndUserId(roomId, userId);
    }
}
