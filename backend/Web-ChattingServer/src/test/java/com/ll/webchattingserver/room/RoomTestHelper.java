package com.ll.webchattingserver.room;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ll.webchattingserver.api.Result;
import com.ll.webchattingserver.domain.user.dto.request.LoginRequest;
import com.ll.webchattingserver.domain.user.dto.request.SignupRequest;
import com.ll.webchattingserver.domain.room.dto.request.RoomCreateRequest;
import com.ll.webchattingserver.domain.user.dto.response.TokenResponse;
import com.ll.webchattingserver.domain.room.dto.response.RoomCreateResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Component
public class RoomTestHelper {
    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private RedisTemplate<String, Object> redisTemplate;

    public void signUp(String username, String email, String password) throws Exception {
        SignupRequest request = SignupRequest.of(username, email, password, password);
        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    public String login(String username, String password) throws Exception {
        LoginRequest request = LoginRequest.of(username, password);
        String responseBody = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        JavaType type = objectMapper.getTypeFactory().constructParametricType(
                Result.class, TokenResponse.class);
        Result<TokenResponse> result = objectMapper.readValue(responseBody, type);
        return result.getData().getToken();
    }

    public String getAuthToken(String username, String email, String password) throws Exception {
        signUp(username, email, password);
        return login(username, password);
    }

    public UUID createRoom(String token, String roomName) throws Exception {
        RoomCreateRequest request = RoomCreateRequest.of(roomName);
        String responseBody = mockMvc.perform(post("/api/room")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        JavaType type = objectMapper.getTypeFactory().constructParametricType(
                Result.class, RoomCreateResponse.class);
        Result<RoomCreateResponse> result = objectMapper.readValue(responseBody, type);
        return UUID.fromString(result.getData().getId());
    }

    public void clearRedisData() {
        Set<String> keys = redisTemplate.keys("chat:*");
        if (!keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }
}