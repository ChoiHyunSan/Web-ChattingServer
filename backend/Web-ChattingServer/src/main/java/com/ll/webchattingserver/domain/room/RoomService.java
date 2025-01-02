package com.ll.webchattingserver.domain.room;

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

        return RoomCreateResponse.of(room.getId().toString());
    }

    public List<RoomListResponse> getRoomList(RoomCond cond) {
        return roomQueryRepository.findByCond(cond)
                .stream()
                .map(RoomListResponse::of)
                .toList();
    }
}
