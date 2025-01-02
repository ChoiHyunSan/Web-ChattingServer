package com.ll.webchattingserver.api.dto.response.room;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomListResponse {
    private UUID id;
    private String name;
    private LocalDateTime createdAt;
    private int participantCount;
}
