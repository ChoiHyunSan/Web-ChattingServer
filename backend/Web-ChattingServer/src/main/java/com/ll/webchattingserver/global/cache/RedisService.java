package com.ll.webchattingserver.global.cache;

import com.ll.webchattingserver.api.dto.redis.RoomRedisDto;
import com.ll.webchattingserver.domain.room.Room;
import com.ll.webchattingserver.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;

    public void setRoom(Room room) {
        String redisKey = "chat:room:" + room.getId();
        redisTemplate.opsForValue().set(redisKey, RoomRedisDto.of(room));
    }

    public void setParticipants(Room room, User user){
        String participantKey = "chat:room" + room.getId() + ":participant";
        redisTemplate.opsForSet().add(participantKey, String.valueOf(user.getId()));
    }
}
