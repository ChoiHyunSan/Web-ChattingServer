package com.ll.webchattingserver.api.v1.controller;

import com.ll.webchattingserver.api.v1.Result;
import com.ll.webchattingserver.core.domain.auth.dto.request.LoginRequest;
import com.ll.webchattingserver.core.domain.auth.dto.request.SignupRequest;
import com.ll.webchattingserver.core.domain.auth.dto.response.SignupResponse;
import com.ll.webchattingserver.core.domain.auth.dto.response.TokenResponse;
import com.ll.webchattingserver.core.domain.auth.service.AuthService;
import com.ll.webchattingserver.core.domain.auth.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Tag(name = "AUTH API", description = "Auth API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final AuthService authService;

    @Operation(
            summary = "로그인 처리",
            description = "로그인을 진행합니다."
    )
    @PostMapping("/login")
    public Result<TokenResponse> login(
            @RequestBody LoginRequest request
    ) {
        // 여기서 Request 를 비즈니스 로직에서 처리하는 어떠한 객체로 변환을 해서 넘기자.
        // ex. LoginRequest -> LoginInfo
        // Bean Validation 을 사용하는 대신, Request에 대한 validate메서드를 만들어서 호출하거나, 비즈니스 객체로 변환하면서 검증할 수도 있다.
        return Result.success(authService.login(request));
    }

    @Operation(
            summary = "로그인 처리",
            description = "로그인을 진행합니다."
    )
    @PostMapping("/refresh")
    public Result<TokenResponse> refresh(
            HttpServletRequest request
    ) {
        return Result.success(authService.refresh(request));
    }

    @Operation(
            summary = "새로운 유저를 생성합니다.",
            description = "새로운 유저 생성"
    )
    @PostMapping("/signup")
    public Result<SignupResponse> signup(
            @RequestBody SignupRequest request
    ) {
        SignupResponse response = userService.signUp(
                request.getUsername(),
                request.getEmail(),
                request.getPassword(),
                request.getPasswordCheck()
        );

        return Result.success(response);
    }
}
