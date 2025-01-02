package com.ll.webchattingserver.api.dto.request;

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
}
