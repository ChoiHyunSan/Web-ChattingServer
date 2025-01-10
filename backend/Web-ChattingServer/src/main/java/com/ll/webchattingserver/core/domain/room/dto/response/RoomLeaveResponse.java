package com.ll.webchattingserver.core.domain.room.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.UUID;

@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "방을 나가는 요청에 대한 응답")
public class RoomLeaveResponse {

    @Schema(description = "요청된 방의 ID")
    private UUID roomId;

    public static RoomLeaveResponse of() {
        return RoomLeaveResponse.builder().build();
    }
}
