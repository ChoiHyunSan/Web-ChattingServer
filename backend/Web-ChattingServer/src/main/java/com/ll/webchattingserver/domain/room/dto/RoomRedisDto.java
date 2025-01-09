package com.ll.webchattingserver.domain.room.dto;

import com.ll.webchattingserver.domain.room.Room;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
@Schema(description = "Redis에 캐싱되는 방에 대한 정보")
public class RoomRedisDto {
    
    @Schema(description = "방 ID")
    private String id;
    
    @Schema(description = "방 이름")
    private String name;
    
    @Schema(description = "방 생성 시간")
    private LocalDateTime createdAt;
    
    @Schema(description = "방 참여 인원")
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
