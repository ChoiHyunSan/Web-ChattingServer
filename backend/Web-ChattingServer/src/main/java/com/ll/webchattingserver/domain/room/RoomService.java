package com.ll.webchattingserver.domain.room;

import com.ll.webchattingserver.api.dto.redis.RoomRedisDto;
import com.ll.webchattingserver.api.dto.response.room.RoomCreateResponse;
import com.ll.webchattingserver.api.dto.response.room.RoomJoinResponse;
import com.ll.webchattingserver.api.dto.response.room.RoomListResponse;
import com.ll.webchattingserver.domain.user.User;
import com.ll.webchattingserver.domain.user.UserService;
import com.ll.webchattingserver.global.cache.RedisService;
import com.ll.webchattingserver.global.exception.ResourceNotFoundException;
import io.lettuce.core.RedisConnectionException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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

    @Transactional
    public RoomCreateResponse create(String name, String roomName) {
        User user = userService.findByUsername(name);

        Room room = Room.of(roomName);
        room.join(user);
        roomRepository.save(room);

        redisService.createRoom(room, user);

        return RoomCreateResponse.of(room.getId().toString());
    }

    public List<RoomListResponse> getRoomList(RoomCond cond) {
        return roomQueryRepository.findByCond(cond);
    }

    public List<RoomListResponse> getMyList(String name) {
        User user = userService.findByUsername(name);

        Optional<List<RoomRedisDto>> userRoomsOpt = redisService.getUserRooms(user);
        return userRoomsOpt.map(roomRedisDtoList ->
                        roomRedisDtoList.stream()
                                .map(RoomListResponse::of)
                                .toList())
                .orElseGet(() -> roomQueryRepository.findByUserContain(user));
    }

    @Transactional
    public RoomJoinResponse join(String name, UUID roomId) {
        User user = userService.findByUsername(name);
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid Room ID: " + roomId));

        try {
              if (redisService.getRoom(roomId).isEmpty()) {
                redisService.setRoom(room);
            }
            redisService.joinRoom(user, roomId);
        } catch (RedisConnectionException e) {
            log.warn("Redis connection failed, proceeding with DB only: {}", e.getMessage());

            // 실패 시, DB 작업
            room.join(user);
            roomRepository.save(room);
        }

        return RoomJoinResponse.of();
    }
}
