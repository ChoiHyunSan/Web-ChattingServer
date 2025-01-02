package com.ll.webchattingserver.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ll.webchattingserver.api.dto.request.LoginRequest;
import com.ll.webchattingserver.api.dto.request.SignupRequest;
import com.ll.webchattingserver.global.exception.DuplicateEmailException;
import com.ll.webchattingserver.global.exception.DuplicateUsernameException;
import com.ll.webchattingserver.global.exception.PasswordMismatchException;
import jakarta.transaction.Transactional;
import org.apache.juli.logging.Log;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AuthControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @Test
    @DisplayName("회원 가입 - 성공")
    void t1() throws Exception {
        signUp();
    }

    private void signUp() throws Exception {
        SignupRequest signupRequest = SignupRequest.of("홍길동", "Test@Test.com", "12345", "12345");
        String content = objectMapper.writeValueAsString(signupRequest);

        mockMvc.perform(post("/api/auth/signup")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(content))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.username").value("홍길동"))
                .andExpect(jsonPath("$.data.email").value("Test@Test.com"));
    }

    @Test
    @DisplayName("회원 가입 - 중복된 닉네임이나 이메일은 회원가입이 불가능하다.")
    void t2() throws Exception {
        SignupRequest signupRequest1 = SignupRequest.of("홍길동", "Test@Test.com", "12345", "12345");
        SignupRequest signupRequest2 = SignupRequest.of("임꺽정", "Test@Test.com", "12345", "12345");
        SignupRequest signupRequest3 = SignupRequest.of("홍길동", "abcd@abcd.com", "12345", "12345");

        mockMvc.perform(post("/api/auth/signup")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(signupRequest1)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/auth/signup")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(signupRequest2)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(DuplicateEmailException.MSG));

        mockMvc.perform(post("/api/auth/signup")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(signupRequest3)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(DuplicateUsernameException.MSG));
    }

    @Test
    @DisplayName("비밀번호 값과 비밀번호 확인 값이 일치하지 않으면 회원가입에 실패한다.")
    void t3() throws Exception {
        SignupRequest signupRequest1 = SignupRequest.of("홍길동", "Test@Test.com", "12345", "123456");
        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest1)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(PasswordMismatchException.MSG));
    }

    @Test
    @DisplayName("로그인 성공")
    void t4() throws Exception {
        signUp();

        LoginRequest loginRequest = LoginRequest.of("홍길동", "12345");
        mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.token").isNotEmpty());
    }

    @Test
    @DisplayName("유효하지 않은 로그인 정보로 시도 시엔 실패한다.")
    void t5() throws Exception {
        signUp();
        LoginRequest loginRequest = LoginRequest.of("임꺽정", "12345");
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("로그인 정보가 올바르지 않습니다."));
    }
}