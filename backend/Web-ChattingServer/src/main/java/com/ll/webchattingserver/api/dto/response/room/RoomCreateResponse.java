package com.ll.webchattingserver.api.dto.response.room;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.data.jpa.domain.AbstractAuditable_;

@Data
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "방 생성 요청에 대한 응답")
public class RoomCreateResponse {

    @Schema(description = "생성된 방의 ID")
    private String id;


    public static RoomCreateResponse of(String id) {
        return RoomCreateResponse.builder()
                .id(id)
                .build();
    }
}