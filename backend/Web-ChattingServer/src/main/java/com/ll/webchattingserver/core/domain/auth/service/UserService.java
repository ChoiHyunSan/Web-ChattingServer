package com.ll.webchattingserver.core.domain.auth.service;

import com.ll.webchattingserver.core.domain.auth.implement.SignupValidator;
import com.ll.webchattingserver.core.domain.auth.implement.UserAppender;
import com.ll.webchattingserver.entity.user.User;
import com.ll.webchattingserver.entity.user.repository.UserRepository;
import com.ll.webchattingserver.core.domain.auth.dto.response.SignupResponse;
import com.ll.webchattingserver.global.exception.clazz.service.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserAppender userAppender;
    private final SignupValidator signupValidator;

    @Transactional
    public SignupResponse signUp(String username, String email, String password, String passwordCheck) {
        signupValidator.checkInvalidSignupInput(username, email, password, passwordCheck);

        User user = userAppender.saveUser(username, password, email);
        return SignupResponse.of(user.getUsername(), user.getEmail());
    }
}
