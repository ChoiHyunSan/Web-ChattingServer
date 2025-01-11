package com.ll.webchattingserver.core.domain.room.implement;

import com.ll.webchattingserver.core.domain.room.dto.RoomCond;
import com.ll.webchattingserver.core.domain.room.dto.RoomRedisDto;
import com.ll.webchattingserver.entity.room.Room;
import com.ll.webchattingserver.entity.room.repository.RoomQueryRepository;
import com.ll.webchattingserver.entity.room.repository.RoomRepository;
import com.ll.webchattingserver.global.exception.clazz.service.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RoomReader {

    private final RoomRepository roomRepository;
    private final RoomQueryRepository roomQueryRepository;

    public List<Room> getRoomList(RoomCond cond) {
        return roomQueryRepository.findByCond(cond);
    }

    public List<Room> findRoomsByUserId(Long id) {
        return roomQueryRepository.findRoomsByUserId(id);
    }

    public Room findRoom(UUID roomId) {
        return roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Room not found: " + roomId));
    }
}
