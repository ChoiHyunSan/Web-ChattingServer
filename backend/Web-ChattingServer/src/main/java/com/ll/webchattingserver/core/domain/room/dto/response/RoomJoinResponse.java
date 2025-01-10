package com.ll.webchattingserver.core.domain.room.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "방 참여 요청에 대한 응답")
public class RoomJoinResponse {

    @Schema(description = "요청한 방에 대한 ID")
    private String roomId;


    public static RoomJoinResponse of(String roomId) {
        return RoomJoinResponse.builder()
                .roomId(roomId)
                .build();
    }
}
