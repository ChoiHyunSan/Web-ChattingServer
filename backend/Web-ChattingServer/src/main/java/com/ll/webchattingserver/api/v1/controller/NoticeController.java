package com.ll.webchattingserver.api.v1.controller;

import com.ll.webchattingserver.api.v1.Result;
import com.ll.webchattingserver.core.domain.notice.dto.request.NoticeCreateRequest;
import com.ll.webchattingserver.core.domain.notice.dto.response.NoticeCreateResponse;
import com.ll.webchattingserver.core.domain.notice.dto.response.NoticeResponse;
import com.ll.webchattingserver.core.domain.notice.service.NoticeService;
import com.ll.webchattingserver.global.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Notice API", description = "Notice API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notice")
public class NoticeController {

    private final NoticeService noticeService;

    @Operation(
            summary = "공지 목록을 요청합니다.",
            description = "공지 목록을 반환"
    )
    @GetMapping
    public Result<List<NoticeResponse>> list(){
        return Result.success(noticeService.getNoticeList());
    }

    @Operation(
            summary = "새로운 공지를 생성합니다.",
            description = "새로운 공지를 저장"
    )
    @PostMapping
    public Result<NoticeCreateResponse> create(
            @RequestBody NoticeCreateRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ){
        NoticeCreateResponse response = noticeService.createNotice(
                principal.getId(),
                principal.getRole(),
                principal.getUsername(),
                request);
        return Result.success(response);
    }
}
