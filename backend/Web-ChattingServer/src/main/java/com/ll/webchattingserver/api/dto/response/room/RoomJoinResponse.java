package com.ll.webchattingserver.api.dto.response.room;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoomJoinResponse {

    public static RoomJoinResponse of() {
        return RoomJoinResponse.builder().build();
    }
}
