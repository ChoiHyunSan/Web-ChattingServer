package com.ll.webchattingserver.core.domain.notice.implement;

import com.ll.webchattingserver.entity.notice.Notice;
import com.ll.webchattingserver.entity.notice.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class NoticeReader {

    private final NoticeRepository noticeRepository;

    public List<Notice> findAll(){
        return noticeRepository.findAll();
    }
}
