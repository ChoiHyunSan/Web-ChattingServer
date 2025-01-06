package com.ll.webchattingserver.api.dto.request.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "User를 생성합니다.")
public class SignupRequest {

    @Schema(description = "유저 이름")
    @NotNull @NotBlank
    private String username;

    @Schema(description = "이메일")
    @NotNull @NotBlank
    private String email;

    @Schema(description = "비밀번호")
    @NotNull @NotBlank
    private String password;

    @Schema(description = "비밀번호 확인")
    @NotNull @NotBlank
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
