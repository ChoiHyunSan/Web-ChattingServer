package com.ll.webchattingserver.global.exception;

public class DuplicateEmailException extends UserSignupException {
    // TODO : 메시지 파일을 따로 모아서 처리하도록 변경
    public final static String MSG = "이미 존재하는 이메일입니다.";
    public DuplicateEmailException() {
      super(MSG);
    }
}
