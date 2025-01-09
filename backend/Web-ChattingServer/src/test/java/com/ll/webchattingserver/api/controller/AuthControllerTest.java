package com.ll.webchattingserver.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ll.webchattingserver.domain.user.dto.request.LoginRequest;
import com.ll.webchattingserver.domain.user.dto.request.SignupRequest;
import com.ll.webchattingserver.global.exception.clazz.service.DuplicateEmailException;
import com.ll.webchattingserver.global.exception.clazz.service.DuplicateUsernameException;
import com.ll.webchattingserver.global.exception.clazz.service.PasswordMismatchException;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AuthControllerTest {
    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    private static final String USERNAME = "홍길동";
    private static final String EMAIL = "Test@Test.com";
    private static final String PASSWORD = "12345";
    private static final String SIGNUP_URL = "/api/auth/signup";
    private static final String LOGIN_URL = "/api/auth/login";

    private SignupRequest createSignupRequest(String username, String email, String password, String passwordConfirm) {
        return SignupRequest.of(username, email, password, passwordConfirm);
    }

    private SignupRequest createValidSignupRequest() {
        return createSignupRequest(USERNAME, EMAIL, PASSWORD, PASSWORD);
    }

    private void performSignup(SignupRequest request) throws Exception {
        mockMvc.perform(post(SIGNUP_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.username").value(request.getUsername()))
                .andExpect(jsonPath("$.data.email").value(request.getEmail()));
    }

    @Nested
    @DisplayName("회원가입")
    class SignUp {
        @Test
        @DisplayName("성공")
        void success() throws Exception {
            SignupRequest request = createValidSignupRequest();
            performSignup(request);
        }

        @Test
        @DisplayName("실패 - 중복된 이메일")
        void failDuplicateEmail() throws Exception {
            // 첫 번째 회원가입
            performSignup(createValidSignupRequest());

            // 중복된 이메일로 회원가입 시도
            SignupRequest duplicateEmail = createSignupRequest("임꺽정", EMAIL, PASSWORD, PASSWORD);
            mockMvc.perform(post(SIGNUP_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(duplicateEmail)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value(DuplicateEmailException.MSG));
        }

        @Test
        @DisplayName("실패 - 중복된 닉네임")
        void failDuplicateUsername() throws Exception {
            // 첫 번째 회원가입
            performSignup(createValidSignupRequest());

            // 중복된 닉네임으로 회원가입 시도
            SignupRequest duplicateUsername = createSignupRequest(USERNAME, "other@test.com", PASSWORD, PASSWORD);
            mockMvc.perform(post(SIGNUP_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(duplicateUsername)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value(DuplicateUsernameException.MSG));
        }

        @Test
        @DisplayName("실패 - 비밀번호 불일치")
        void failPasswordMismatch() throws Exception {
            SignupRequest request = createSignupRequest(USERNAME, EMAIL, PASSWORD, PASSWORD + "wrong");
            mockMvc.perform(post(SIGNUP_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value(PasswordMismatchException.MSG));
        }
    }

    @Nested
    @DisplayName("로그인")
    class Login {
        @BeforeEach
        void setUp() throws Exception {
            performSignup(createValidSignupRequest());
        }

        @Test
        @DisplayName("성공")
        void success() throws Exception {
            LoginRequest loginRequest = LoginRequest.of(USERNAME, PASSWORD);
            mockMvc.perform(post(LOGIN_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.token").isNotEmpty());
        }

        @Test
        @DisplayName("실패 - 유효하지 않은 로그인 정보")
        void failInvalidCredentials() throws Exception {
            LoginRequest loginRequest = LoginRequest.of("임꺽정", PASSWORD);
            mockMvc.perform(post(LOGIN_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.message").value("로그인 정보가 올바르지 않습니다."));
        }
    }
}