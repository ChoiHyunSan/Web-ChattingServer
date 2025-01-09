package com.ll.webchattingserver.global.exception.clazz.security;

public class InvalidTokenAccessException extends RuntimeException {
    public InvalidTokenAccessException() {
        super("Invalid token");
    }
}
