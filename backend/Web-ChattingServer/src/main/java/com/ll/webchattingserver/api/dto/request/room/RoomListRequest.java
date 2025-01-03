package com.ll.webchattingserver.api.dto.request.room;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoomListRequest {
    private String roomName;
    private Integer page;
    private Integer size;
    private String sort;
}
