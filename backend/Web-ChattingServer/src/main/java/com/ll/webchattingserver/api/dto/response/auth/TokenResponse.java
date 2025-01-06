package com.ll.webchattingserver.api.dto.response.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "생성된 토큰을 반환합니다.")
public class TokenResponse {
    
    @Schema(description = "토큰 값")
    private String token;
    
    @Schema(description = "토큰을 가진 유저의 이름")
    private String username;

    public static TokenResponse of(String token, String username) {
        return TokenResponse.builder()
                .token(token)
                .username(username)
                .build();
    }
}
