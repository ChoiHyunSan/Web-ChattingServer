package com.ll.webchattingserver.global.exception;

import com.ll.webchattingserver.api.controller.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserSignupException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleUserSignupException(UserSignupException e) {
        return Result.error(400, e.getMessage());
    }
}
