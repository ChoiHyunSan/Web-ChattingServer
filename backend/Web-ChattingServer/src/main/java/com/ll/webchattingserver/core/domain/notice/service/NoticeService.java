package com.ll.webchattingserver.core.domain.notice.service;

import com.ll.webchattingserver.core.domain.notice.Notice;
import com.ll.webchattingserver.core.domain.notice.dto.request.NoticeCreateRequest;
import com.ll.webchattingserver.core.domain.notice.dto.response.NoticeCreateResponse;
import com.ll.webchattingserver.core.domain.notice.dto.response.NoticeResponse;
import com.ll.webchattingserver.core.domain.notice.repository.NoticeRepository;
import com.ll.webchattingserver.core.domain.user.User;
import com.ll.webchattingserver.core.domain.user.UserRole;
import com.ll.webchattingserver.core.domain.user.service.UserService;
import com.ll.webchattingserver.global.exception.clazz.service.NoAuthorizeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final UserService userService;

    public List<NoticeResponse> getNoticeList() {
        return noticeRepository.findAll().stream()
                .map(NoticeResponse::of)
                .toList();
    }

    public NoticeCreateResponse createNotice(Long userId, String role, NoticeCreateRequest request) {
        if(!role.equals(UserRole.ROLE_ADMIN.getValue())) {
            throw new NoAuthorizeException("공지사항을 작성할 권한이 없습니다. UserID : " + userId + ", Role : " + role);
        }

        User author = userService.findById(userId);
        Notice notice = Notice.builder()
                .author(author)
                .content(request.getContent())
                .build();

        noticeRepository.save(notice);
        return NoticeCreateResponse.of(notice);
    }
}
