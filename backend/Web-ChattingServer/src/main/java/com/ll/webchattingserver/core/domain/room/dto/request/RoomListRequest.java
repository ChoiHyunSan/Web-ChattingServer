package com.ll.webchattingserver.core.domain.room.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "방 목록을 요청합니다.")
public class RoomListRequest {
    
    @Schema(description = "방 이름")
    private String roomName;
    
    @Schema(description = "페이지")
    private Integer page;
    
    @Schema(description = "페이징 사이즈")
    private Integer size;
    
    @Schema(description = "정렬 기준")
    private String sort;
}
