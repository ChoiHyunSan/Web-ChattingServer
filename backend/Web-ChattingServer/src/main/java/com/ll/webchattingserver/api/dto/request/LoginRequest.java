package com.ll.webchattingserver.api.dto.request;

import lombok.*;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class LoginRequest {
    private String username;
    private String password;
}
