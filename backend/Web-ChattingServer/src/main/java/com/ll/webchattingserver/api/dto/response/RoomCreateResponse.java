package com.ll.webchattingserver.api.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RoomCreateResponse {
    private String id;

    public static RoomCreateResponse of(String id) {
        return RoomCreateResponse.builder()
                .id(id)
                .build();
    }
}
