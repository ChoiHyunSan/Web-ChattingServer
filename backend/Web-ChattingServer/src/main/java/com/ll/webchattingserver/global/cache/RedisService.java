package com.ll.webchattingserver.global.cache;

import com.ll.webchattingserver.api.dto.redis.RoomRedisDto;
import com.ll.webchattingserver.domain.room.Room;
import com.ll.webchattingserver.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisTemplate<String, Long> redisLongTemplate;
    /**
     * Key : RoomId / Value : Set<userId>
     */
    private static String generateRoomUserSetKey(UUID roomId) {
        return "chat:room" + roomId + ":participant";
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

    public void setRoom(Room room) {
        String redisKey = generateRoomInfoKey(room.getId());
        redisTemplate.opsForValue().set(redisKey, RoomRedisDto.of(room));
    }

    public void setParticipants(UUID roomId, Long userId){
        String participantKey = generateRoomUserSetKey(roomId);
        redisTemplate.opsForSet().add(participantKey, userId);
    }

    public void mappingUserAndRoom(User user, UUID roomId) {
        String roomsKey = generateUserRoomSetKey(user);
        redisTemplate.opsForSet().add(roomsKey, roomId);
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
        return getRoom(generateRoomInfoKey(id));
    }

    public Optional<RoomRedisDto> getRoom(String roomInfoKey){
        return Optional.ofNullable((RoomRedisDto)redisTemplate.opsForValue().get(roomInfoKey));
    }

    public void joinRoom(User user, UUID roomId) {
        redisTemplate.execute(new SessionCallback<Void>() {
            @Override
            public Void execute(RedisOperations operations) throws DataAccessException {
                operations.multi();

                // 참여 정보 저장
                setParticipants(roomId, user.getId());
                mappingUserAndRoom(user, roomId);

                operations.exec();
                return null;
            }
        });
    }

    public void createRoom(Room room, User user) {
        redisTemplate.execute(new SessionCallback<Void>() {
            @Override
            public Void execute(RedisOperations operations) throws DataAccessException {
                operations.multi();

                setRoom(room);
                setParticipants(room.getId(), user.getId());
                mappingUserAndRoom(user, room.getId());

                operations.exec();
                return null;
            }
        });
    }

    public Set<String> getRoomkeys() {
        return  redisTemplate.keys("chat:room:*");
    }

    public Set<Long> getParticipants(UUID roomId) {
        String key = generateRoomUserSetKey(roomId);
        return redisLongTemplate.opsForSet().members(key);
    }
}
