package com.ll.webchattingserver.core.domain.notice.service;

import com.ll.webchattingserver.core.domain.notice.implement.NoticeAppender;
import com.ll.webchattingserver.core.domain.notice.implement.NoticeReader;
import com.ll.webchattingserver.core.domain.notice.implement.NoticeValidator;
import com.ll.webchattingserver.entity.notice.Notice;
import com.ll.webchattingserver.core.domain.notice.dto.request.NoticeCreateRequest;
import com.ll.webchattingserver.core.domain.notice.dto.response.NoticeCreateResponse;
import com.ll.webchattingserver.core.domain.notice.dto.response.NoticeResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeReader noticeReader;
    private final NoticeAppender noticeAppender;
    private final NoticeValidator noticeValidator;

    public List<NoticeResponse> getNoticeList() {
        return noticeReader.findAll().stream()
                .map(NoticeResponse::of)
                .toList();
    }

    public NoticeCreateResponse createNotice(Long userId, String userRole, String username, NoticeCreateRequest request) {
        noticeValidator.validate(userId, userRole);
        Long noticeId = noticeAppender.save(Notice.builder()
                .author(username)
                .content(request.getContent())
                .build());
        return NoticeCreateResponse.of(noticeId, request.getContent());
    }
}
