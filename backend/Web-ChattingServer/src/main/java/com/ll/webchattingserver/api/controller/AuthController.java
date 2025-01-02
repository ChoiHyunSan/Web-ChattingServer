package com.ll.webchattingserver.api.controller;

import com.ll.webchattingserver.api.Result;
import com.ll.webchattingserver.api.dto.request.auth.LoginRequest;
import com.ll.webchattingserver.api.dto.request.auth.SignupRequest;
import com.ll.webchattingserver.api.dto.response.auth.SignupResponse;
import com.ll.webchattingserver.api.dto.response.auth.TokenResponse;
import com.ll.webchattingserver.domain.user.UserService;
import com.ll.webchattingserver.global.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final JwtProvider jwtProvider;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;

    @PostMapping("/login")
    public Result<TokenResponse> login(@RequestBody LoginRequest request) {
        authenticationManager.authenticate( new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        String token = jwtProvider.createToken(request.getUsername(), "ROLE_USER");
        return Result.success(TokenResponse.of(token, request.getUsername()));
    }

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
