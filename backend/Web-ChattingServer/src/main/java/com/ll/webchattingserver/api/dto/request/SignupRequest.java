package com.ll.webchattingserver.api.dto.request;

import lombok.Data;

@Data
public class SignupRequest {
    private String username;
    private String email;
    private String password;
    private String passwordCheck;
}
