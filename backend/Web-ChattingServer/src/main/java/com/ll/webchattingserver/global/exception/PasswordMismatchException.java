package com.ll.webchattingserver.global.exception;

public class PasswordMismatchException extends UserSignupException {
    public PasswordMismatchException() {
        // TODO : 메시지 파일을 따로 모아서 처리하도록 변경
        super("비밀번호와 비밀번호 확인 값이 일치하지 않습니다.");
    }
}
