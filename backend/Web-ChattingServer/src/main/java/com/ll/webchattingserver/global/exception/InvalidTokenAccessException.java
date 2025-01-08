package com.ll.webchattingserver.global.exception;

import org.springframework.security.core.AuthenticationException;

public class InvalidTokenAccessException extends RuntimeException {
    public InvalidTokenAccessException() {
        super("Invalid token");
    }
}
