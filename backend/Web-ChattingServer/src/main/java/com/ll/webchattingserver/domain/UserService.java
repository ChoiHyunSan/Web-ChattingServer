package com.ll.webchattingserver.domain;

import com.ll.webchattingserver.api.dto.response.SignupResponse;
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
            // TODO : 아이디 중복 예외 반환
        };
        if(!password.equals(passwordCheck)) {
            // TODO : 비밀번호 <-> 비밀번호 확인 값 불일치 예외 반환
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
