package com.ll.webchattingserver.domain.user.service;

import com.ll.webchattingserver.domain.user.User;
import com.ll.webchattingserver.domain.user.repository.UserRepository;
import com.ll.webchattingserver.domain.user.dto.response.SignupResponse;
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

    @Transactional
    public SignupResponse signUp(String username, String email, String password, String passwordCheck) {
        checkInvalidSignupInput(username, email, password, passwordCheck);

        User user = userRepository.save(User.of(username, passwordEncoder.encode(password), email, "ROLE_USER"));
        return SignupResponse.of(user.getUsername(), user.getEmail());
    }

    private void checkInvalidSignupInput(String username, String email, String password, String passwordCheck) {
        if(!password.equals(passwordCheck)) {
            throw new PasswordMismatchException();
        }

        Optional<User> optionalUser = userRepository.findByUsernameOrEmail(username, email);
        if(optionalUser.isPresent()){
            User user = optionalUser.get();
            throw user.getUsername().equals(username)
                    ? new DuplicateUsernameException()
                    : new DuplicateEmailException();
        }
    }

    public User findByUsername(String name) {
        return userRepository.findByUsername(name).orElseThrow(() -> new ResourceNotFoundException(User.class.getPackageName()));
    }

    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(User.class.getPackageName()));
    }

    public boolean checkInvalidUser(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    public boolean existsByUsername(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    public void save(User user) {
        userRepository.save(user);
    }
}
