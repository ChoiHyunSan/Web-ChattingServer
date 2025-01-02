package com.ll.webchattingserver.api.dto.response;

import lombok.*;

@Data
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class TokenResponse {
    private String token;
    private String username;

    public static TokenResponse of(String token, String username) {
        return TokenResponse.builder()
                .token(token)
                .username(username)
                .build();
    }
}
