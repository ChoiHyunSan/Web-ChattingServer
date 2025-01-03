package com.ll.webchattingserver.api.dto.redis;

import com.ll.webchattingserver.domain.room.Room;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public class RoomRedisDto {
    private String id;
    private String name;
    private LocalDateTime createdAt;
    private int participantCount;

    // QueryDSL용 생성자
    public RoomRedisDto(UUID id, String name, LocalDateTime createdAt, Integer participantCount) {
        this.id = id.toString();
        this.name = name;
        this.createdAt = createdAt;
        this.participantCount = participantCount;
    }

    public static RoomRedisDto of(Room room) {
        return RoomRedisDto.builder()
                .id(room.getId().toString())
                .name(room.getName())
                .createdAt(room.getCreatedAt())
                .participantCount(room.getParticipantCount())
                .build();
    }

    public int leaveOneUser() {
        return --participantCount;
    }
}
