package com.ll.webchattingserver.global.exception.clazz.service;

public class DuplicateUsernameException extends UserSignupException {
    // TODO : 메시지 파일을 따로 모아서 처리하도록 변경
    public final static String MSG = "이미 존재하는 회원 입니다.";
    public DuplicateUsernameException() {
        super(MSG);
    }
}
