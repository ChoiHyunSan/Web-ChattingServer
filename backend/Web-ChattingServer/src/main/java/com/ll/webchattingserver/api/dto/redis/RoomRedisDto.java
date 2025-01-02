package com.ll.webchattingserver.api.dto.redis;

import com.ll.webchattingserver.domain.room.Room;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RoomRedisDto {
    private UUID id;
    private String name;
    private LocalDateTime createdAt;
    private int participantCount;

    public static RoomRedisDto of(Room room) {
        return RoomRedisDto.builder()
                .id(room.getId())
                .name(room.getName())
                .createdAt(room.getCreatedAt())
                .participantCount(room.getParticipants().size())
                .build();
    }
}
