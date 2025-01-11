package com.ll.webchattingserver.core.domain.notice.implement;

import com.ll.webchattingserver.core.enums.UserRole;
import com.ll.webchattingserver.global.exception.clazz.service.NoAuthorizeException;
import org.springframework.stereotype.Component;

@Component
public class NoticeValidator {

    public void validate(Long userId, String role) {
        if(role.equals(UserRole.ROLE_ADMIN.getValue())) {
            throw new NoAuthorizeException("공지사항을 작성할 권한이 없습니다. UserID : " + userId + ", Role : " + role);
        }
    }
}
