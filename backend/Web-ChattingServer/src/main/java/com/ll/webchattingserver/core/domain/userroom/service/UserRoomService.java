package com.ll.webchattingserver.core.domain.userroom.service;

import com.ll.webchattingserver.entity.room.Room;
import com.ll.webchattingserver.entity.user.User;
import com.ll.webchattingserver.entity.userroom.UserRoom;
import com.ll.webchattingserver.entity.userroom.repository.UserRoomQueryRepository;
import com.ll.webchattingserver.entity.userroom.repository.UserRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

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

    public List<Room> findRoomsByUserId(Long id) {
        return userRoomQueryRepository.findRoomsByUserId(id);
    }

    public List<UserRoom> findByUserAndRoom(User user, Room room) {
       return userRoomQueryRepository.findByUserAndRoom(user, room);
    }

    public void deleteUserRoom(UUID roomId, Long userId) {
        userRoomQueryRepository.deleteByRoomIdAndUserId(roomId, userId);
    }
}
