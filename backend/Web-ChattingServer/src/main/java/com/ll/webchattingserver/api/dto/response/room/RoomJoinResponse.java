package com.ll.webchattingserver.api.dto.response.room;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoomJoinResponse {

    private String roomId;

    public static RoomJoinResponse of() {
        return RoomJoinResponse.builder().build();
    }
}
