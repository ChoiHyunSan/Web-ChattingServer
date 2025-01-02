package com.ll.webchattingserver.global.exception;

import com.ll.webchattingserver.api.controller.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({UserSignupException.class, DataIntegrityViolationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleUserSignupException(RuntimeException e) {
        if(e instanceof  DataIntegrityViolationException) {
            log.error("처리되지 않은 예외가 발생 : {}", e.getMessage());
            return Result.error(400, "회원가입 오류가 발생했습니다.");
        }

        return Result.error(400, e.getMessage());
    }

    @ExceptionHandler({AuthenticationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleUsernameNotFoundException(RuntimeException e) {
        return Result.error(400, "로그인 정보가 올바르지 않습니다.");
    }
}
