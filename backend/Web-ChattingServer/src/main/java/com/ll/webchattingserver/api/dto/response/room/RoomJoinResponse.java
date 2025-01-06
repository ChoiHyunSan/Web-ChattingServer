package com.ll.webchattingserver.api.dto.response.room;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
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
