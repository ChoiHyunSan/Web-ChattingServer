package com.ll.webchattingserver.global.security;

import com.ll.webchattingserver.api.Result;
import com.ll.webchattingserver.global.exception.InvalidTokenAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class SecurityExceptionHandler {

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(UsernameNotFoundException.class)
    public Result<Void> handleUsernameNotFoundException(UsernameNotFoundException e) {
        return Result.error(401, "사용자를 찾을 수 없습니다.");
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler({AuthenticationException.class, InvalidTokenAccessException.class})
    public Result<Void> handleAuthenticationException(AuthenticationException e) {
        return Result.error(401, "인증에 실패했습니다.");
    }
}
