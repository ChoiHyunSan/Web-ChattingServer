package com.ll.webchattingserver.core.domain.userroom.implement;

import com.ll.webchattingserver.entity.room.Room;
import com.ll.webchattingserver.entity.user.User;
import com.ll.webchattingserver.entity.userroom.UserRoom;
import com.ll.webchattingserver.entity.userroom.repository.UserRoomQueryRepository;
import com.ll.webchattingserver.entity.userroom.repository.UserRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UserRoomReader {

    private final UserRoomRepository userRoomRepository;
    private final UserRoomQueryRepository userRoomQueryRepository;

    public List<UserRoom> findByRoom(Room room) {
        return userRoomRepository.findByRoom(room);
    }

    public List<Room> findRoomsByUserId(Long id) {
        return userRoomQueryRepository.findRoomsByUserId(id);
    }

    public List<UserRoom> findByUserAndRoom(User user, Room room) {
        return userRoomQueryRepository.findByUserAndRoom(user, room);
    }
}
