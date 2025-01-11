package com.ll.webchattingserver.core.domain.auth.dto.response;

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

    @Schema(description = "리프래시 토큰 값")
    private String refreshToken;

    @Schema(description = "토큰을 가진 유저의 이름")
    private String username;

    public static TokenResponse of(String token, String refreshToken, String username) {
        return TokenResponse.builder()
                .token(token)
                .refreshToken(refreshToken)
                .username(username)
                .build();
    }
}
