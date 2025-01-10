package com.ll.webchattingserver.core.domain.notice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "공지사항 생성 요청")
public class NoticeCreateRequest {

    @Schema(description = "공지사항 내용")
    private String content;
}
