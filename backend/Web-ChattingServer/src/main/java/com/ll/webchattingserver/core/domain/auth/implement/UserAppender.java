package com.ll.webchattingserver.core.domain.auth.implement;

import com.ll.webchattingserver.core.enums.UserRole;
import com.ll.webchattingserver.entity.user.User;
import com.ll.webchattingserver.entity.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserAppender {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public Long saveAdminUser(User user) {
        if(!user.getRole().equals(UserRole.ROLE_ADMIN)){
            // TODO : 권한에 해당하지 않는 유저를 저장하려 시도 중. 예외 처리
        }

        User adminUser = userRepository.save(user);
        return adminUser.getId();
    }

    public User saveUser(String username, String password, String email) {
        return userRepository.save(User.of(username, passwordEncoder.encode(password), email, "ROLE_USER"));
    }
}
