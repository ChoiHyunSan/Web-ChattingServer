package com.ll.webchattingserver.domain.user.controller.api;

import com.ll.webchattingserver.api.Result;
import com.ll.webchattingserver.domain.user.dto.request.LoginRequest;
import com.ll.webchattingserver.domain.user.dto.request.SignupRequest;
import com.ll.webchattingserver.domain.user.dto.response.SignupResponse;
import com.ll.webchattingserver.domain.user.dto.response.TokenResponse;
import com.ll.webchattingserver.domain.user.service.AuthService;
import com.ll.webchattingserver.domain.user.service.UserService;
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
    public Result<SignupResponse> signup(@RequestBody SignupRequest request) {
        SignupResponse response = userService.signUp(
                request.getUsername(),
                request.getEmail(),
                request.getPassword(),
                request.getPasswordCheck()
        );

        return Result.success(response);
    }
}
