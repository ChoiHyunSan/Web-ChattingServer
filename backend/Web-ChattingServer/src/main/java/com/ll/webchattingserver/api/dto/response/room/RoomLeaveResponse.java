package com.ll.webchattingserver.api.dto.response.room;

import com.ll.webchattingserver.domain.room.Room;
import lombok.*;

import java.util.UUID;

@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RoomLeaveResponse {

    private UUID roomId;

    public static RoomLeaveResponse of() {
        return RoomLeaveResponse.builder().build();
    }
}
