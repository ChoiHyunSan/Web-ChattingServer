package com.ll.webchattingserver.core.domain.room.service;

import com.ll.webchattingserver.core.domain.auth.implement.UserReader;
import com.ll.webchattingserver.core.domain.userroom.implement.UserRoomManager;
import com.ll.webchattingserver.core.domain.userroom.implement.UserRoomReader;
import com.ll.webchattingserver.entity.room.Room;
import com.ll.webchattingserver.core.domain.room.dto.RoomCond;
import com.ll.webchattingserver.core.domain.room.dto.RoomRedisDto;
import com.ll.webchattingserver.core.domain.room.dto.response.RoomCreateResponse;
import com.ll.webchattingserver.core.domain.room.dto.response.RoomJoinResponse;
import com.ll.webchattingserver.core.domain.room.dto.response.RoomLeaveResponse;
import com.ll.webchattingserver.entity.room.repository.RoomQueryRepository;
import com.ll.webchattingserver.entity.room.repository.RoomRepository;
import com.ll.webchattingserver.entity.userroom.UserRoom;
import com.ll.webchattingserver.entity.user.User;
import com.ll.webchattingserver.global.exception.clazz.service.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final RoomQueryRepository roomQueryRepository;
    private final UserReader userReader;
    private final UserRoomManager userRoomManager;
    private final UserRoomReader userRoomReader;

    @Transactional()
    public RoomCreateResponse create(String name, String roomName) {
        User user = userReader.findByUsername(name);
        Room room = createRoom(roomName);
        userRoomManager.createUserRoom(user, room);
        return RoomCreateResponse.of(room.getId().toString());
    }

    public List<RoomRedisDto> getRoomList(RoomCond cond) {
        return roomQueryRepository.findByCond(cond);
    }

    public List<RoomRedisDto> getMyList(Long userId) {
        log.info("레디스가 비어있네요");
        List<Room> rooms = userRoomReader.findRoomsByUserId(userId);
        List<RoomRedisDto> roomDtos = rooms.stream()
                .map(RoomRedisDto::of)
                .toList();
        return roomDtos;
    }

    @Transactional
    public RoomJoinResponse join(String name, UUID roomId) {
        User user = userReader.findByUsername(name);
        Room room = findRoom(roomId);

        if(userRoomReader.findByUserAndRoom(user, room).isEmpty()){
            userRoomManager.createUserRoom(user, room);
        }
        return RoomJoinResponse.of(roomId.toString());
    }

    @Transactional
    public RoomLeaveResponse leave(Long userId, UUID roomId){
        Room room = findRoom(roomId);

        List<UserRoom> userRooms = userRoomReader.findByRoom(room);
        try {
            // UserRoom 데이터 삭제
            userRoomManager.deleteUserRoom(roomId, userId);

            // 방이 사라지는 경우 (= 자기 자신 밖에 방에 없는 경우)
            if (userRooms.size() == 1) {
                // 채팅방 삭제 + 레디스 방 정보 삭제
                roomRepository.delete(room);
            }
        } catch (Exception e) {
            log.warn("Failed to update Redis cache for room leave: room={}, user={}", roomId, userId);
        }
        return RoomLeaveResponse.of();
    }

    // 헬퍼 메서드들
    private Room createRoom(String roomName) {
        Room room = Room.of(roomName);
        return roomRepository.save(room);
    }

    private Room findRoom(UUID roomId) {
        return roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Room not found: " + roomId));
    }
}
