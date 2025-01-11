package com.ll.webchattingserver.core.domain.notice.implement;

import com.ll.webchattingserver.entity.notice.Notice;
import com.ll.webchattingserver.entity.notice.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NoticeAppender {

    private final NoticeRepository noticeRepository;

    public Long save(Notice notice) {
        return noticeRepository.save(notice).getId();
    }
}
