package com.ll.webchattingserver.api.dto.request;

import lombok.*;

@Data
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class RoomCreateRequest {
    private String roomName;

    public static RoomCreateRequest of(String roomName) {
        return RoomCreateRequest.builder().roomName(roomName).build();
    }
}
