package com.ll.webchattingserver.global.cache;

import com.ll.webchattingserver.core.domain.room.dto.RoomRedisDto;
import com.ll.webchattingserver.entity.room.Room;
import com.ll.webchattingserver.entity.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;

    /**
     *  Key : RoomId / Value : RoomRedisDto
     */
    private static String generateRoomInfoKey(UUID roomId) {
        return "chat:room:" + roomId;
    }
    private static String generateRoomInfoKey(String roomId) {
        return "chat:room:" + roomId;
    }


    /**
     *  Key : User / Value : Set<roomID>
     */
    private static String generateUserRoomSetKey(Long userId) {
        return "chat:user:" + userId + ":rooms";
    }

    // 기본 캐싱 작업
    public void setRoom(Room room) {
        setRoom(RoomRedisDto.of(room));
    }

    public void setRoom(RoomRedisDto room) {
        try {
            String redisKey = generateRoomInfoKey(UUID.fromString(room.getId()));
            redisTemplate.opsForValue().set(redisKey, room);
            log.info("RedisKey : {}, value : {}", redisKey, room);
        } catch (Exception e) {
            log.warn("Failed to cache room: {}", room.getId(), e);
        }
    }

    public void mappingUserAndRoom(Long userId, UUID roomId) {
        try {
            String roomsKey = generateUserRoomSetKey(userId);
            redisTemplate.opsForSet().add(roomsKey, roomId);
            log.info("RoomsKey : {}, roomId : {}", roomsKey, roomId);
        } catch (Exception e) {
            log.warn("Failed to map user to room in cache: user={}, room={}", userId, roomId, e);
        }
    }

    public Optional<List<RoomRedisDto>> getUserRooms(Long userId) {

        String key = generateUserRoomSetKey(userId);
        Set<Object> roomIds = redisTemplate.opsForSet().members(key);

        log.info("RoomIds: {}", roomIds);
        if (roomIds == null || roomIds.isEmpty()) {
            return Optional.empty();
        }

        List<RoomRedisDto> list = roomIds.stream()
                .map(id -> getRoom(generateRoomInfoKey(id.toString())).orElse(null))
                .filter(Objects::nonNull)
                .toList();

        return list.isEmpty() ? Optional.empty() : Optional.of(list);
    }

    public Optional<RoomRedisDto> getRoom(UUID id) {
        return getRoom(generateRoomInfoKey(id));
    }

    public Optional<RoomRedisDto> getRoom(String roomInfoKey){
        log.info("RoomInfo key: {}", roomInfoKey);
        return Optional.ofNullable((RoomRedisDto)redisTemplate.opsForValue().get(roomInfoKey));
    }

    public Set<String> getRoomKeys() {
        return  redisTemplate.keys("chat:room:*");
    }

    // 방 참가 관련 캐싱
    public void joinRoom(Long userId, UUID roomId) {
        try {
            mappingUserAndRoom(userId, roomId);
            log.debug("User {} joined room {} in cache", userId, roomId);
        } catch (Exception e) {
            log.warn("Failed to cache room join: user={}, room={}", userId, roomId, e);
        }
    }

    public void createRoom(Room room, User user) {
        try {
            setRoom(room);
            mappingUserAndRoom(user.getId(), room.getId());
            log.debug("Room created in cache: {}", room.getId());
        } catch (Exception e) {
            log.warn("Failed to cache new room: {}", room.getId(), e);
        }
    }

    // 방 나가기 관련 캐싱
    public void leaveRoom(Long userId, UUID roomId) {
        try {
            String userRoomSetKey = generateUserRoomSetKey(userId);
            redisTemplate.opsForSet().remove(userRoomSetKey, roomId);
            log.debug("User {} left room {} in cache", userId, roomId);
        } catch (Exception e) {
            log.warn("Failed to cache room leave: user={}, room={}", userId, roomId, e);
        }
    }

    // 방 삭제 관련 캐싱
    public void removeRoom(UUID roomId) {
        try {
            String roomInfoKey = generateRoomInfoKey(roomId);
            redisTemplate.delete(roomInfoKey);
            log.debug("Room {} removed from cache", roomId);
        } catch (Exception e) {
            log.warn("Failed to remove room from cache: {}", roomId, e);
        }
    }

    public void cacheUserRooms(Long userId, List<RoomRedisDto> rooms) {
        rooms.forEach(room -> {
            setRoom(room);
            mappingUserAndRoom(userId ,UUID.fromString(room.getId()));
        });
    }

    // 방 정보와 사용자 정보를 함께 캐싱
    public void cacheRoomWithUser(Room room, Long userId) {
        setRoom(room);
        mappingUserAndRoom(userId, room.getId());
    }

    // 방 나가기 시 캐시 업데이트
    public void updateRoomCache(Room room, Long userId) {
        if (getRoom(room.getId()).isPresent()) {
            setRoom(room);
        }
        leaveRoom(userId, room.getId());
    }
}


