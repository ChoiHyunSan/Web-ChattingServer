package com.ll.webchattingserver.core.domain.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Schema(name = "User를 요청합니다.")
public class LoginRequest {

    @Schema(description = "유저 이름")
    @NotNull @NotBlank
    private String username;

    @Schema(description = "비밀번호")
    @NotNull @NotBlank
    private String password;

    public static LoginRequest of(String username, String password) {
        return LoginRequest.builder()
                .username(username)
                .password(password)
                .build();
    }
}
