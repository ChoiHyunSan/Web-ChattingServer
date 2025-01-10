package com.ll.webchattingserver.global.exception.handler;

import com.ll.webchattingserver.api.v1.Result;
import com.ll.webchattingserver.global.exception.clazz.security.InvalidTokenAccessException;
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
    @ExceptionHandler({InvalidTokenAccessException.class})
    public Result<Void> handleAuthenticationException(AuthenticationException e) {
        return Result.error(401, "인증에 실패했습니다.");
    }

    @ExceptionHandler({AuthenticationException.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Result<Void> handleUsernameNotFoundException(RuntimeException e) {
        return Result.error(401, "로그인 정보가 올바르지 않습니다.");
    }
}
