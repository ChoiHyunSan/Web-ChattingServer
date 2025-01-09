package com.ll.webchattingserver.domain.user.controller.api;

import com.ll.webchattingserver.api.Result;
import com.ll.webchattingserver.domain.user.dto.request.LoginRequest;
import com.ll.webchattingserver.domain.user.dto.request.SignupRequest;
import com.ll.webchattingserver.domain.user.dto.response.SignupResponse;
import com.ll.webchattingserver.domain.user.dto.response.TokenResponse;
import com.ll.webchattingserver.domain.user.service.UserService;
import com.ll.webchattingserver.global.security.jwt.JwtProvider;
import com.ll.webchattingserver.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Slf4j
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
        String newAccessToken = jwtProvider.createAccessToken(userDetails.getUsername(), userDetails.getId());
        String newRefreshToken = jwtProvider.createRefreshToken(userDetails.getUsername(), userDetails.getId());

        return Result.success(TokenResponse.of(
                newAccessToken,
                newRefreshToken,
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
        jwtProvider.validateRefreshToken(extractToken);

        String username = jwtProvider.getUsernameByRefreshToken(extractToken);
        Long userId = jwtProvider.getUserIdByRefreshToken(extractToken);
        log.info("Username: {}, UserId: {}", username, userId);

        String newAccessToken = jwtProvider.createAccessToken(username, userId);
        String newRefreshToken = jwtProvider.createRefreshToken(username, userId);

        // SecurityContext 갱신
        Authentication authentication = jwtProvider.getAuthentication(newAccessToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        log.info("Refresh! New Access Token: {}, New Refresh Token: {}", newAccessToken, newRefreshToken);

        return Result.success(TokenResponse.of(
                newAccessToken,
                newRefreshToken,
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
