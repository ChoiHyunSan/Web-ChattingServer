package com.ll.webchattingserver.global.exception.clazz.service;

public class NoAuthorizeException extends RuntimeException {
    public NoAuthorizeException(String message) {
        super(message);
    }
}
