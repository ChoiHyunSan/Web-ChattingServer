package com.ll.webchattingserver.global.exception.handler;

import com.ll.webchattingserver.api.Result;
import com.ll.webchattingserver.global.exception.clazz.redis.LogicErrorException;
import com.ll.webchattingserver.global.exception.clazz.service.UserSignupException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 *   비즈니스 로직 처리 중 발생한 예외처리 핸들러
 */

@Slf4j
@RestControllerAdvice
public class ServiceExceptionHandler {

    @ExceptionHandler({UserSignupException.class, DataIntegrityViolationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleUserSignupException(RuntimeException e) {
        if(e instanceof  DataIntegrityViolationException) {
            log.error("처리되지 않은 예외가 발생 : {}", e.getMessage());
            return Result.error(400, "회원가입 오류가 발생했습니다.");
        }

        return Result.error(400, e.getMessage());
    }

    @ExceptionHandler({LogicErrorException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Void> handleIllegalAccessException(RuntimeException e) {
        log.warn(e.getMessage());
        return Result.error(500,"Server Error");
    }
}
