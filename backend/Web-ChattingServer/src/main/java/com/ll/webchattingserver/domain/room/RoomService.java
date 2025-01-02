package com.ll.webchattingserver.domain.room;

import com.ll.webchattingserver.api.dto.redis.RoomRedisDto;
import com.ll.webchattingserver.api.dto.response.RoomCreateResponse;
import com.ll.webchattingserver.domain.user.User;
import com.ll.webchattingserver.domain.user.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final UserService userService;
    private final RedisTemplate<String, Object> redisTemplate;

    @Transactional
    public RoomCreateResponse create(String name, String roomName) {
        User user = userService.findByUsername(name);

        Room room = Room.of(roomName);
        room.join(user);
        roomRepository.save(room);

        String redisKey = "chat:room:" + room.getId();
        redisTemplate.opsForValue().set(redisKey, RoomRedisDto.of(room));

        String participantKey = "chat:room" + room.getId() + ":participant";
        redisTemplate.opsForSet().add(participantKey, String.valueOf(user.getId()));

        return RoomCreateResponse.of(room.getId().toString());
    }
}
