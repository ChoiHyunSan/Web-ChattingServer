package com.ll.webchattingserver.domain.intermediate;

import com.ll.webchattingserver.domain.room.Room;
import com.ll.webchattingserver.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserRoomService {

    private final UserRoomRepository userRoomRepository;
    private final UserRoomQueryRepository userRoomQueryRepository;

    public void createUserRoom(User user, Room room) {
        UserRoom userRoom = UserRoom.of(user, room);
        user.getUserRooms().add(userRoom);
        room.getUserRooms().add(userRoom);
        userRoomRepository.save(userRoom);
    }

    public List<UserRoom> findByRoom(Room room) {
        return userRoomRepository.findByRoom(room);
    }

    public void delete(UserRoom userRoom) {
        userRoomRepository.delete(userRoom);
    }

    public List<Room> findRoomsByUserId(Long id) {
        return userRoomQueryRepository.findRoomsByUserId(id);
    }
}
