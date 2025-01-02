package com.ll.webchattingserver.domain.room;

import com.ll.webchattingserver.api.dto.redis.RoomRedisDto;
import com.ll.webchattingserver.api.dto.response.room.RoomCreateResponse;
import com.ll.webchattingserver.api.dto.response.room.RoomListResponse;
import com.ll.webchattingserver.domain.user.User;
import com.ll.webchattingserver.domain.user.UserService;
import com.ll.webchattingserver.global.cache.RedisService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

        redisService.setRoom(room);
        redisService.setParticipants(room, user);
        redisService.mappingUserAndRoom(user, room);

        return RoomCreateResponse.of(room.getId().toString());
    }

    public List<RoomListResponse> getRoomList(RoomCond cond) {
        return roomQueryRepository.findByCond(cond);
    }

    public List<RoomListResponse> getMyList(String name) {
        User user = userService.findByUsername(name);

        Optional<List<RoomRedisDto>> userRoomsOpt = redisService.getUserRooms(user);
        if(userRoomsOpt.isPresent()) {
            return userRoomsOpt.get().stream().map(RoomListResponse::of).toList();
        }

        return roomQueryRepository.findByUserContain(user);
    }
}
