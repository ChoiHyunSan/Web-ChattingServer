package com.ll.webchattingserver.api.dto.response.room;

import lombok.*;

@Data
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RoomCreateResponse {
    private String id;

    public static RoomCreateResponse of(String id) {
        return RoomCreateResponse.builder()
                .id(id)
                .build();
    }
}
