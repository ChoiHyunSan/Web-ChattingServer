package com.ll.webchattingserver.core.domain.room.service;

import com.ll.webchattingserver.core.domain.room.implement.RoomManager;
import com.ll.webchattingserver.core.domain.room.implement.RoomReader;
import com.ll.webchattingserver.core.domain.userroom.implement.UserRoomManager;
import com.ll.webchattingserver.core.domain.userroom.implement.UserRoomReader;
import com.ll.webchattingserver.entity.room.Room;
import com.ll.webchattingserver.core.domain.room.dto.RoomCond;
import com.ll.webchattingserver.core.domain.room.dto.RoomRedisDto;
import com.ll.webchattingserver.core.domain.room.dto.response.RoomCreateResponse;
import com.ll.webchattingserver.core.domain.room.dto.response.RoomJoinResponse;
import com.ll.webchattingserver.core.domain.room.dto.response.RoomLeaveResponse;
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

    private final UserRoomManager userRoomManager;
    private final UserRoomReader userRoomReader;
    private final RoomManager roomManager;
    private final RoomReader roomReader;

    @Transactional()
    public RoomCreateResponse create(Long userId, String roomName) {
        Room room = roomManager.createRoom(roomName);
        roomManager.joinRoom(userId, room.getId());
        return RoomCreateResponse.of(room.getId().toString());
    }

    @Transactional
    public RoomJoinResponse join(Long userId, UUID roomId) {
        roomManager.joinRoom(userId, roomId);
        return RoomJoinResponse.of(roomId.toString());
    }

    public List<RoomRedisDto> getRoomList(RoomCond cond) {
        return roomReader.getRoomList(cond).stream()
                .map(RoomRedisDto::of)
                .toList();
    }

    public List<RoomRedisDto> getUserList(Long userId) {
        return roomReader.findRoomsByUserId(userId).stream()
                .map(RoomRedisDto::of)
                .toList();
    }

    @Transactional
    public RoomLeaveResponse leave(Long userId, UUID roomId){
        roomManager.leaveRoom(userId, roomId);
        return RoomLeaveResponse.of();
    }
}
