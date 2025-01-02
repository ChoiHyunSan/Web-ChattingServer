package com.ll.webchattingserver.api.dto.response.room;

import com.ll.webchattingserver.api.dto.redis.RoomRedisDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomListResponse {
    private UUID id;
    private String name;
    private LocalDateTime createdAt;
    private int participantCount;

    public static RoomListResponse of(RoomRedisDto roomRedisDto) {
        return RoomListResponse.builder()
                .id(roomRedisDto.getId())
                .name(roomRedisDto.getName())
                .createdAt(roomRedisDto.getCreatedAt())
                .participantCount(roomRedisDto.getParticipantCount())
                .build();
    }
}
