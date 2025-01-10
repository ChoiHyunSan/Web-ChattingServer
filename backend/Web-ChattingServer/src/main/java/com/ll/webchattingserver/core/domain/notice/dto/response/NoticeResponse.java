package com.ll.webchattingserver.core.domain.notice.dto.response;

import com.ll.webchattingserver.core.domain.notice.Notice;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "방 정보")
public class NoticeResponse {

    @Schema(description = "공지 사항 생성 시간")
    private LocalDateTime noticeTime;
    
    @Schema(description = "공지사항 내용")
    private String content;

    public static NoticeResponse of(Notice notice) {
        return NoticeResponse.builder()
                .noticeTime(notice.getCreatedAt())
                .content(notice.getContent())
                .build();
    }
}
