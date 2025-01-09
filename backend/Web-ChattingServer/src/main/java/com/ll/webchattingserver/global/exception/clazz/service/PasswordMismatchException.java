package com.ll.webchattingserver.global.exception.clazz.service;

public class PasswordMismatchException extends UserSignupException {
    // TODO : 메시지 파일을 따로 모아서 처리하도록 변경
    public final static String MSG = "비밀번호와 비밀번호 확인 값이 일치하지 않습니다.";
    public PasswordMismatchException() {
        // TODO : 메시지 파일을 따로 모아서 처리하도록 변경
        super(MSG);
    }
}
