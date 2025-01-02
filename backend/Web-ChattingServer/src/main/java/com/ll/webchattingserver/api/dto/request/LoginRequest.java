package com.ll.webchattingserver.api.dto.request;

import lombok.*;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class LoginRequest {
    private String username;
    private String password;

    public static LoginRequest of(String username, String password) {
        return LoginRequest.builder()
                .username(username)
                .password(password)
                .build();
    }
}
