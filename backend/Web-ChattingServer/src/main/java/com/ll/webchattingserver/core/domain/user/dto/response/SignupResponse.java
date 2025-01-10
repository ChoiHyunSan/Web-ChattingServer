package com.ll.webchattingserver.core.domain.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "회원가입 요청 응답")
public class SignupResponse {
    
    @Schema(description = "생성된 유저 이름")
    private String username;
    
    @Schema(description = "생성된 유저의 이메일")
    private String email;

    public static SignupResponse of(String username, String email) {
        return SignupResponse.builder()
                .username(username)
                .email(email)
                .build();
    }
}
