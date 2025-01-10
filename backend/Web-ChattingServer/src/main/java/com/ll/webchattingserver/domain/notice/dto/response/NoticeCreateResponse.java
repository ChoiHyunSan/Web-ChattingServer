package com.ll.webchattingserver.domain.notice.dto.response;

import com.ll.webchattingserver.domain.notice.Notice;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "공지사항 생성에 대한 응답")
public class NoticeCreateResponse {

    @Schema(description = "생성된 공지사항의 번호")
    private Long id;

    @Schema(description = "생성된 공지사항 내용")
    private String content;

    public static NoticeCreateResponse of(Notice notice) {
        return NoticeCreateResponse.builder()
                .id(notice.getId())
                .content(notice.getContent())
                .build();
    }
}
