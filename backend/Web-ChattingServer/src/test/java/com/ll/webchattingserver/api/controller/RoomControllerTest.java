package com.ll.webchattingserver.api.controller;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ll.webchattingserver.api.Result;
import com.ll.webchattingserver.api.dto.redis.RoomRedisDto;
import com.ll.webchattingserver.api.dto.request.auth.LoginRequest;
import com.ll.webchattingserver.api.dto.request.room.RoomCreateRequest;
import com.ll.webchattingserver.api.dto.request.auth.SignupRequest;
import com.ll.webchattingserver.api.dto.response.auth.TokenResponse;
import com.ll.webchattingserver.api.dto.response.room.RoomCreateResponse;
import com.ll.webchattingserver.domain.room.Room;
import com.ll.webchattingserver.domain.room.RoomRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class RoomControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private RoomRepository roomRepository;
    @Autowired private RedisTemplate<String, Object> redisTemplate;

    private String token;

    private void signUpAndLogin() throws Exception {
        SignupRequest signupRequest = SignupRequest.of("홍길동", "Test@Test.com", "12345", "12345");
        String content = objectMapper.writeValueAsString(signupRequest);

        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content));

        LoginRequest loginRequest = LoginRequest.of("홍길동", "12345");
        String responseBody = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andReturn().getResponse().getContentAsString();

        JavaType type = objectMapper.getTypeFactory().constructParametricType(
                Result.class,
                TokenResponse.class
        );

        Result<TokenResponse> result = objectMapper.readValue(responseBody, type);
        token = result.getData().getToken();
        assertThat(token).isNotNull();
    }

    @BeforeEach
    void setUp() throws Exception {
        signUpAndLogin();
    }

    @AfterEach
    void tearDown() {
        // Redis는 @Transactional의 영향을 받지 않으므로 따로 제거해준다.
        String pattern = "chat:room:*";
        Set<String> keys = redisTemplate.keys(pattern);
        redisTemplate.delete(keys);
    }

    @Test
    @DisplayName("방이 정상적으로 생성되고, DB와 Redis에 정상적으로 저장된다.")
    void t1() throws Exception {
        RoomCreateRequest request = RoomCreateRequest.of("New Room");
        String responseBody = mockMvc.perform(post("/api/room")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        JavaType type = objectMapper.getTypeFactory().constructParametricType(
                Result.class,
                RoomCreateResponse.class
        );
        Result<RoomCreateResponse> result = objectMapper.readValue(responseBody, type);
        UUID roomId = UUID.fromString(result.getData().getId());

        Room room = roomRepository.findById(roomId).get();
        assertThat(room.getName()).isEqualTo("New Room");
        assertThat(room.getParticipants().size()).isEqualTo(1);

        String redisKey = "chat:room:" + room.getId();
        RoomRedisDto dto = (RoomRedisDto) redisTemplate.opsForValue().get(redisKey);
        assertThat(dto).isNotNull();
        assertThat(dto.getName()).isEqualTo("New Room");
        assertThat(dto.getParticipantCount()).isEqualTo(1);
        assertThat(dto.getId()).isEqualTo(roomId);
    }

    @Test
    @DisplayName("회원은 현재 만들어진 모든 방에 대한 정보를 요청할 수 있다.")
    void t2() throws Exception {

    }

    @Test
    @DisplayName("회원은 현재 자신이 속한 모든 방에 대한 정보를 요청할 수 있다.")
    void t3() throws Exception {

    }
}