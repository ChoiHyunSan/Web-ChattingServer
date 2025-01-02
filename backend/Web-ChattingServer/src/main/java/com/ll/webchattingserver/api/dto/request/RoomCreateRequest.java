package com.ll.webchattingserver.api.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RoomCreateRequest {
    private String roomName;
}
