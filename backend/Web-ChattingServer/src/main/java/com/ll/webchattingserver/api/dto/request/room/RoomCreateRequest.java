package com.ll.webchattingserver.api.dto.request.room;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "방을 생성합니다.")
public class RoomCreateRequest {
    
    @Schema(description = "방 이름")
    @NotBlank @NotNull
    private String roomName;

    public static RoomCreateRequest of(String roomName) {
        return RoomCreateRequest.builder().roomName(roomName).build();
    }
}
