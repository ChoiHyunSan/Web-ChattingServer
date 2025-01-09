package com.ll.webchattingserver.domain.room.service;

import com.ll.webchattingserver.domain.room.Room;
import com.ll.webchattingserver.domain.room.dto.RoomCond;
import com.ll.webchattingserver.domain.room.dto.RoomRedisDto;
import com.ll.webchattingserver.domain.room.dto.response.RoomCreateResponse;
import com.ll.webchattingserver.domain.room.dto.response.RoomJoinResponse;
import com.ll.webchattingserver.domain.room.dto.response.RoomLeaveResponse;
import com.ll.webchattingserver.domain.room.repository.RoomQueryRepository;
import com.ll.webchattingserver.domain.room.repository.RoomRepository;
import com.ll.webchattingserver.domain.username.UserRoom;
import com.ll.webchattingserver.domain.username.service.UserRoomService;
import com.ll.webchattingserver.domain.user.User;
import com.ll.webchattingserver.domain.user.UserService;
import com.ll.webchattingserver.global.cache.RedisService;
import com.ll.webchattingserver.global.exception.clazz.service.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final RoomQueryRepository roomQueryRepository;

    private final UserService userService;
    private final RedisService redisService;
    private final UserRoomService userRoomService;

    @Transactional()
    public RoomCreateResponse create(String name, String roomName) {

        User user = userService.findByUsername(name);
        Room room = createRoom(roomName);
        userRoomService.createUserRoom(user, room);

        try {
            redisService.createRoom(room, user);
        } catch (Exception e) {
            log.warn("Failed to cache new room: room={}, user={}", room.getId(), user.getId(), e);
        }

        return RoomCreateResponse.of(room.getId().toString());
    }

    public List<RoomRedisDto> getRoomList(RoomCond cond) {
        return roomQueryRepository.findByCond(cond);
    }

    public List<RoomRedisDto> getMyList(Long userId) {
        Optional<List<RoomRedisDto>> userRooms = redisService.getUserRooms(userId);
        if(userRooms.isPresent()) {
            return userRooms.get();
        }

        log.info("레디스가 비어있네요");

        List<Room> rooms = userRoomService.findRoomsByUserId(userId);
        List<RoomRedisDto> roomDtos = rooms.stream()
                .map(RoomRedisDto::of)
                .toList();

        // 캐시 갱신
        try {
            redisService.cacheUserRooms(userId, roomDtos);
        } catch (Exception e) {
            log.warn("Failed to cache user rooms: user={}", userId, e);
        }

        return roomDtos;
    }

    @Transactional
    public RoomJoinResponse join(String name, UUID roomId) {
        User user = userService.findByUsername(name);
        Room room = findRoom(roomId);

        if(userRoomService.findByUserAndRoom(user, room).isEmpty()){
            userRoomService.createUserRoom(user, room);
        }

        // Redis 캐시 갱신 시도
        try {
            // 방 정보가 캐시에 있다면 갱신
            if (redisService.getRoom(roomId).isPresent()) {
                redisService.setRoom(room);
            }
            redisService.joinRoom(user.getId(), roomId);
        } catch (Exception e) {
            log.warn("Failed to update Redis cache for room join: room={}, user={}", roomId, user.getId());
        }

        return RoomJoinResponse.of(roomId.toString());
    }

    @Transactional
    public RoomLeaveResponse leave(Long userId, UUID roomId){
        Room room = findRoom(roomId);

        List<UserRoom> userRooms = userRoomService.findByRoom(room);
        try {
            // UserRoom 데이터 삭제
            userRoomService.deleteUserRoom(roomId, userId);

            // 방이 사라지는 경우 (= 자기 자신 밖에 방에 없는 경우)
            if (userRooms.size() == 1) {
                // 채팅방 삭제 + 레디스 방 정보 삭제
                roomRepository.delete(room);
                redisService.removeRoom(roomId);
            } else {
                // 방이 유지되는 경우 캐시 갱신
                if (redisService.getRoom(roomId).isPresent()) {
                    redisService.setRoom(room);
                }
            }
        } catch (Exception e) {
            log.warn("Failed to update Redis cache for room leave: room={}, user={}", roomId, userId);
        }

        // 레디스 상에서 유저의 방 목록을 제거
        redisService.leaveRoom(userId, roomId);
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
