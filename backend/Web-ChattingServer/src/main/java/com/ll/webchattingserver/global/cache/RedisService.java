package com.ll.webchattingserver.global.cache;

import com.ll.webchattingserver.api.dto.redis.RoomRedisDto;
import com.ll.webchattingserver.domain.room.Room;
import com.ll.webchattingserver.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;

    public void setRoom(Room room) {
        String redisKey = generateRoomInfoKey(room.getId());
        redisTemplate.opsForValue().set(redisKey, RoomRedisDto.of(room));
    }

    public void setParticipants(Room room, User user){
        String participantKey = generateRoomUserSetKey(room);
        redisTemplate.opsForSet().add(participantKey, user.getId());
    }

    public void mappingUserAndRoom(User user, Room room) {
        String roomsKey = generateUserRoomSetKey(user);
        redisTemplate.opsForSet().add(roomsKey, room.getId());
    }

    public Optional<List<RoomRedisDto>> getUserRooms(User user) {
        String key = generateUserRoomSetKey(user);
        Set<Object> roomIds = redisTemplate.opsForSet().members(key);

        if (roomIds == null || roomIds.isEmpty()) {
            return Optional.of(Collections.emptyList());
        }

        return Optional.of(roomIds.stream()
                .map(id -> getRoom((UUID)id).orElse(null))
                .filter(Objects::nonNull)
                .toList());
    }

    public Optional<RoomRedisDto> getRoom(UUID id) {
        String key = generateRoomInfoKey(id);
        return Optional.ofNullable((RoomRedisDto)redisTemplate.opsForValue().get(key));
    }

    /**
     * Key : RoomId / Value : Set<userId>
     */
    private static String generateRoomUserSetKey(Room room) {
        return "chat:room" + room.getId() + ":participant";
    }

    /**
     *  Key : RoomId / Value : RoomRedisDto
     */
    private static String generateRoomInfoKey(UUID roomId) {
        return "chat:room:" + roomId;
    }

    /**
     *  Key : User / Value : Set<roomID>
     */
    private static String generateUserRoomSetKey(User user) {
        return "chat:user:" + user.getId() + ":rooms";
    }
}
