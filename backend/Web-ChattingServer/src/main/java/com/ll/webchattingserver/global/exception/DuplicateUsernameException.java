package com.ll.webchattingserver.global.exception;

public class DuplicateUsernameException extends UserSignupException {
    public DuplicateUsernameException() {
        // TODO : 메시지 파일을 따로 모아서 처리하도록 변경
        super("이미 존재하는 회원 입니다.");
    }
}
