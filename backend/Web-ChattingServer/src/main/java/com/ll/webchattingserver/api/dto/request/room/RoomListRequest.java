package com.ll.webchattingserver.api.dto.request.room;

import lombok.Data;

@Data
public class RoomListRequest {
    private String roomName;
    private Integer page;
    private Integer size;
    private String sort;
}
