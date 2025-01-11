package com.ll.webchattingserver.core.domain.auth.service;

import com.ll.webchattingserver.core.domain.auth.implement.SignupValidator;
import com.ll.webchattingserver.entity.user.User;
import com.ll.webchattingserver.entity.user.repository.UserRepository;
import com.ll.webchattingserver.core.domain.auth.dto.response.SignupResponse;
import com.ll.webchattingserver.global.exception.clazz.service.DuplicateEmailException;
import com.ll.webchattingserver.global.exception.clazz.service.DuplicateUsernameException;
import com.ll.webchattingserver.global.exception.clazz.service.PasswordMismatchException;
import com.ll.webchattingserver.global.exception.clazz.service.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SignupValidator signupValidator;

    @Transactional
    public SignupResponse signUp(String username, String email, String password, String passwordCheck) {
        signupValidator.checkInvalidSignupInput(username, email, password, passwordCheck);

        User user = userRepository.save(User.of(username, passwordEncoder.encode(password), email, "ROLE_USER"));
        return SignupResponse.of(user.getUsername(), user.getEmail());
    }

    public User findByUsername(String name) {
        return userRepository.findByUsername(name).orElseThrow(() -> new ResourceNotFoundException(User.class.getPackageName()));
    }

    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(User.class.getPackageName()));
    }
}
