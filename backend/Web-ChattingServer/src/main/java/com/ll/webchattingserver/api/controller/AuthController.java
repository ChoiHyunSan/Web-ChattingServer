package com.ll.webchattingserver.api.controller;

import com.ll.webchattingserver.api.Result;
import com.ll.webchattingserver.api.dto.request.auth.LoginRequest;
import com.ll.webchattingserver.api.dto.request.auth.SignupRequest;
import com.ll.webchattingserver.api.dto.response.auth.SignupResponse;
import com.ll.webchattingserver.api.dto.response.auth.TokenResponse;
import com.ll.webchattingserver.domain.user.UserService;
import com.ll.webchattingserver.global.jwt.JwtProvider;
import com.ll.webchattingserver.global.security.CustomUserDetails;
import com.ll.webchattingserver.global.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Tag(name = "AUTH API", description = "Auth API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final JwtProvider jwtProvider;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;

    @Operation(
            summary = "로그인 처리",
            description = "로그인을 진행합니다."
    )
    @PostMapping("/login")
    public Result<TokenResponse> login(
            @RequestBody LoginRequest request
    ) {
        Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        CustomUserDetails userDetails = (CustomUserDetails) authenticate.getPrincipal();
        return Result.success(TokenResponse.of(
                jwtProvider.createAccessToken(userDetails.getUser()),
                jwtProvider.createRefreshToken(userDetails.getUser()),
                request.getUsername()));
    }

    @Operation(
            summary = "로그인 처리",
            description = "로그인을 진행합니다."
    )
    @PostMapping("/refresh")
    public Result<TokenResponse> login(
            @RequestHeader("Authorization") String token
    ) {
        String extractToken = jwtProvider.extractToken(token);
        if(jwtProvider.validateToken(extractToken)) {

        }

        String username = jwtProvider.getUsername(extractToken);
        Long userId = jwtProvider.getUserId(extractToken);

        return Result.success(TokenResponse.of(
                jwtProvider.createAccessToken(username, userId),
                jwtProvider.createRefreshToken(username, userId),
                username));
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
