package com.ll.webchattingserver.global.exception.clazz.redis;

public class LogicErrorException extends RuntimeException {
    public LogicErrorException(String message) {
        super(message);
    }
}
