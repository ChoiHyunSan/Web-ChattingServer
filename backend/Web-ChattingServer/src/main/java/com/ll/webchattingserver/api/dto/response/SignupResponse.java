package com.ll.webchattingserver.api.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SignupResponse {
    private String username;
    private String email;

    public static SignupResponse of(String username, String email) {
        return SignupResponse.builder()
                .username(username)
                .email(email)
                .build();
    }
}
