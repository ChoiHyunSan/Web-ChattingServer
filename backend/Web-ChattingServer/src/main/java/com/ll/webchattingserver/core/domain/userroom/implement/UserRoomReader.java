package com.ll.webchattingserver.core.domain.userroom.implement;

import com.ll.webchattingserver.entity.room.Room;
import com.ll.webchattingserver.entity.userroom.UserRoom;
import com.ll.webchattingserver.entity.userroom.repository.UserRoomQueryRepository;
import com.ll.webchattingserver.entity.userroom.repository.UserRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserRoomReader {

    private final UserRoomRepository userRoomRepository;
    private final UserRoomQueryRepository userRoomQueryRepository;

    public List<UserRoom> findByRoom(Room room) {
        return userRoomRepository.findByRoomId(room.getId());
    }

    public List<UserRoom> findByUsernameAndRoomId(Long userId, UUID roomId) {
        return userRoomQueryRepository.findByUserAndRoom(userId, roomId);
    }
}
