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
import com.ll.webchattingserver.domain.user.User;
import com.ll.webchattingserver.domain.user.UserRepository;
import com.ll.webchattingserver.domain.user.UserService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
        tearDown();
        signUpAndLogin();
    }

    @AfterEach
    void tearDown() {
        // Redis의 모든 키 삭제
        Set<String> keys = redisTemplate.keys("chat:*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
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
        assertThat(room.getParticipantCount()).isEqualTo(1);

        String redisKey = "chat:room:" + room.getId();
        RoomRedisDto dto = (RoomRedisDto) redisTemplate.opsForValue().get(redisKey);
        assertThat(dto).isNotNull();
        assertThat(dto.getName()).isEqualTo("New Room");
        assertThat(dto.getParticipantCount()).isEqualTo(1);
        assertThat(dto.getId()).isEqualTo(roomId.toString());
    }

    @Test
    @DisplayName("회원은 현재 만들어진 모든 방에 대한 정보를 요청할 수 있다.")
    void t2() throws Exception {
        // 첫 번째 사용자(홍길동)가 방 2개 생성
        List<UUID> roomIds = new ArrayList<>();
        for (int i = 1; i <= 2; i++) {
            RoomCreateRequest request = RoomCreateRequest.of("Room " + i);
            String responseBody = mockMvc.perform(post("/api/room")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            JavaType type = objectMapper.getTypeFactory().constructParametricType(
                    Result.class,
                    RoomCreateResponse.class
            );
            Result<RoomCreateResponse> result = objectMapper.readValue(responseBody, type);
            roomIds.add(UUID.fromString(result.getData().getId()));
        }

        // 두 번째 사용자 생성 및 로그인
        SignupRequest signupRequest = SignupRequest.of("김철수", "kim@test.com", "12345", "12345");
        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signupRequest)));

        LoginRequest loginRequest = LoginRequest.of("김철수", "12345");
        String responseBody = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andReturn().getResponse().getContentAsString();

        JavaType tokenType = objectMapper.getTypeFactory().constructParametricType(
                Result.class,
                TokenResponse.class
        );
        Result<TokenResponse> tokenResult = objectMapper.readValue(responseBody, tokenType);
        String token2 = tokenResult.getData().getToken();

        // 두 번째 사용자가 방 1개 생성
        RoomCreateRequest request = RoomCreateRequest.of("Room 3");
        responseBody = mockMvc.perform(post("/api/room")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", "Bearer " + token2))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        JavaType type = objectMapper.getTypeFactory().constructParametricType(
                Result.class,
                RoomCreateResponse.class
        );
        Result<RoomCreateResponse> result = objectMapper.readValue(responseBody, type);
        roomIds.add(UUID.fromString(result.getData().getId()));

        // Redis에서 전체 방 목록 캐시를 제거하여 캐시 미스 상황 만들기
        redisTemplate.delete("chat:rooms");

        // 첫 번째 사용자로 전체 방 목록 조회
        responseBody = mockMvc.perform(get("/api/room/list")
                        .param("roomName", "Room")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "createdAt,desc")
                        .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        JavaType listType = objectMapper.getTypeFactory().constructParametricType(
                Result.class,
                objectMapper.getTypeFactory().constructCollectionType(List.class, RoomRedisDto.class)
        );
        Result<List<RoomRedisDto>> listResult = objectMapper.readValue(responseBody, listType);
        List<RoomRedisDto> rooms = listResult.getData();

        // API 응답 검증
        assertThat(rooms).hasSize(3);  // 총 3개의 방이 조회되어야 함
        assertThat(rooms).extracting("name")
                .containsExactlyInAnyOrder("Room 1", "Room 2", "Room 3");  // 다른 사용자가 만든 방도 포함
        assertThat(rooms.stream()
                .map(RoomRedisDto::getId)
                .map(Object::toString)
                .collect(Collectors.toList()))
                .containsExactlyInAnyOrderElementsOf(
                        roomIds.stream()
                                .map(UUID::toString)
                                .collect(Collectors.toList())
                );
        assertThat(rooms).extracting("participantCount")
                .containsOnly(1);

        // 1. 개별 방 정보 캐시 확인
        for (UUID roomId : roomIds) {
            String roomKey = "chat:room:" + roomId;
            RoomRedisDto cachedRoom = (RoomRedisDto) redisTemplate.opsForValue().get(roomKey);

            assertThat(cachedRoom).isNotNull();
            assertThat(cachedRoom.getId()).isEqualTo(roomId.toString());
            assertThat(cachedRoom.getParticipantCount()).isEqualTo(1);
        }

        // 2. 두 번째 사용자로도 동일한 결과가 조회되는지 확인
        responseBody = mockMvc.perform(get("/api/room/list")
                        .param("roomName", "Room")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "createdAt,desc")
                        .header("Authorization", "Bearer " + token2))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Result<List<RoomRedisDto>> result2 = objectMapper.readValue(responseBody, listType);
        List<RoomRedisDto> rooms2 = result2.getData();

        assertThat(rooms2).hasSize(3);
        assertThat(rooms2).extracting("name")
                .containsExactlyInAnyOrder("Room 1", "Room 2", "Room 3");
    }

    @Test
    @DisplayName("회원은 현재 자신이 속한 모든 방에 대한 정보를 요청할 수 있다.")
    void t3() throws Exception {
        // 방 3개 생성
        List<UUID> roomIds = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            RoomCreateRequest request = RoomCreateRequest.of("My Room " + i);
            String responseBody = mockMvc.perform(post("/api/room")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            JavaType type = objectMapper.getTypeFactory().constructParametricType(
                    Result.class,
                    RoomCreateResponse.class
            );
            Result<RoomCreateResponse> result = objectMapper.readValue(responseBody, type);
            roomIds.add(UUID.fromString(result.getData().getId()));
        }

        // Redis에서 사용자의 방 목록 캐시를 제거하여 캐시 미스 상황 만들기
        redisTemplate.delete("chat:user:홍길동:rooms");

        // 내 방 목록 조회
        String responseBody = mockMvc.perform(get("/api/room/myList")
                        .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        JavaType type = objectMapper.getTypeFactory().constructParametricType(
                Result.class,
                objectMapper.getTypeFactory().constructCollectionType(List.class, RoomRedisDto.class)
        );
        Result<List<RoomRedisDto>> result = objectMapper.readValue(responseBody, type);
        List<RoomRedisDto> myRooms = result.getData();

        // API 응답 검증
        assertThat(myRooms).hasSize(3);
        assertThat(myRooms).extracting("name")
                .containsExactlyInAnyOrder("My Room 1", "My Room 2", "My Room 3");
        assertThat(myRooms.stream()
                .map(RoomRedisDto::getId)
                .map(Object::toString)
                .collect(Collectors.toList()))
                .containsExactlyInAnyOrderElementsOf(
                        roomIds.stream()
                                .map(UUID::toString)
                                .collect(Collectors.toList())
                );
        assertThat(myRooms).extracting("participantCount")
                .containsOnly(1);

        // Redis 캐시 검증
        User user = userService.findByUsername("홍길동");
        String userRoomsKey = "chat:user:" + user.getId() + ":rooms";
        log.info("UserRoomKey = {}", userRoomsKey);
        Set<Object> cachedRooms = redisTemplate.opsForSet().members(userRoomsKey);

        assertThat(cachedRooms).isNotNull();
        assertThat(cachedRooms).hasSize(3);
        assertThat(cachedRooms.stream()
                .map(obj -> UUID.fromString((String) obj))
                .collect(Collectors.toList()))
                .containsExactlyInAnyOrderElementsOf(roomIds);

        // 개별 방 정보도 Redis에 캐시되었는지 확인
        for (UUID roomId : roomIds) {
            String roomKey = "chat:room:" + roomId;
            RoomRedisDto cachedRoom = (RoomRedisDto) redisTemplate.opsForValue().get(roomKey);

            assertThat(cachedRoom).isNotNull();
            assertThat(cachedRoom.getId()).isEqualTo(roomId.toString());
            assertThat(cachedRoom.getParticipantCount()).isEqualTo(1);
        }
    }

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    private static final Logger log = LoggerFactory.getLogger(RoomControllerTest.class);
}