package com.ll.webchattingserver.core.domain.auth.implement;

import com.ll.webchattingserver.entity.user.User;
import com.ll.webchattingserver.global.exception.clazz.service.DuplicateEmailException;
import com.ll.webchattingserver.global.exception.clazz.service.DuplicateUsernameException;
import com.ll.webchattingserver.global.exception.clazz.service.PasswordMismatchException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SignupValidator {

    private final UserReader userReader;

    public void checkInvalidSignupInput(String username, String email, String password, String passwordCheck) {
        if(!password.equals(passwordCheck)) {
            throw new PasswordMismatchException();
        }

        Optional<User> optionalUser = userReader.findByUsernameOrEmail(username, email);
        if(optionalUser.isPresent()){
            User user = optionalUser.get();
            throw user.getUsername().equals(username)
                    ? new DuplicateUsernameException()
                    : new DuplicateEmailException();
        }
    }
}
