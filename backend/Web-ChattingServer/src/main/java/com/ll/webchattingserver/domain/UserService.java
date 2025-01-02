package com.ll.webchattingserver.domain;

import com.ll.webchattingserver.api.dto.response.SignupResponse;
import com.ll.webchattingserver.global.exception.DuplicateUsernameException;
import com.ll.webchattingserver.global.exception.PasswordMismatchException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public SignupResponse signUp(String username, String email, String password, String passwordCheck) {
        if(userRepository.findByUsername(username).isPresent()) {
            throw new DuplicateUsernameException();
        };
        if(!password.equals(passwordCheck)) {
            throw new PasswordMismatchException();
        }

        User user = User.builder()
                .username(username)
                .email(email)
                .password(passwordEncoder.encode(password))
                .build();
        userRepository.save(user);

        return SignupResponse.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }
}
