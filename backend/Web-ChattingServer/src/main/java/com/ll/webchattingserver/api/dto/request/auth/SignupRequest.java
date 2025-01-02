package com.ll.webchattingserver.api.dto.request.auth;

import lombok.*;

@Data
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class SignupRequest {
    private String username;
    private String email;
    private String password;
    private String passwordCheck;

    public static SignupRequest of(String username, String email, String password, String passwordCheck) {
        return SignupRequest.builder()
                .username(username)
                .email(email)
                .password(password)
                .passwordCheck(passwordCheck)
                .build();
    }
}
