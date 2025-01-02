package com.ll.webchattingserver.domain.user;

import com.ll.webchattingserver.api.dto.response.SignupResponse;
import com.ll.webchattingserver.global.exception.DuplicateEmailException;
import com.ll.webchattingserver.global.exception.DuplicateUsernameException;
import com.ll.webchattingserver.global.exception.PasswordMismatchException;
import com.ll.webchattingserver.global.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public SignupResponse signUp(String username, String email, String password, String passwordCheck) {
        checkInvalidSignupInput(username, email, password, passwordCheck);

        User user = userRepository.save(User.of(username, passwordEncoder.encode(password), email));
        return SignupResponse.of(user.getUsername(), user.getEmail());
    }

    private void checkInvalidSignupInput(String username, String email, String password, String passwordCheck) {
        if(userRepository.findByUsername(username).isPresent()) {
            throw new DuplicateUsernameException();
        };

        if(userRepository.findByEmail(email).isPresent()){
            throw new DuplicateEmailException();
        }

        if(!password.equals(passwordCheck)) {
            throw new PasswordMismatchException();
        }
    }

    public User findByUsername(String name) {
        return userRepository.findByUsername(name).orElseThrow(() -> new ResourceNotFoundException(User.class.getPackageName()));
    }
}