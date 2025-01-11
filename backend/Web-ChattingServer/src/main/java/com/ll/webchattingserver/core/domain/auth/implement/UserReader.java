package com.ll.webchattingserver.core.domain.auth.implement;

import com.ll.webchattingserver.entity.user.User;
import com.ll.webchattingserver.entity.user.repository.UserRepository;
import com.ll.webchattingserver.global.exception.clazz.service.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserReader {

    private final UserRepository userRepository;

    public Optional<User> findByUsernameOrEmail(String username, String email) {
        return userRepository.findByUsernameOrEmail(username, email);
    }

    public boolean existsByUsername(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(User.class.getPackageName()));
    }

    public User findByUsername(String name) {
        return userRepository.findByUsername(name).orElseThrow(() -> new ResourceNotFoundException(User.class.getPackageName()));
    }
}
