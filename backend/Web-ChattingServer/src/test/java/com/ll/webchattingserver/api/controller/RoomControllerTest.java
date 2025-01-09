package com.ll.webchattingserver.api.controller;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ll.webchattingserver.api.Result;
import com.ll.webchattingserver.domain.room.dto.RoomRedisDto;
import com.ll.webchattingserver.domain.room.Room;
import com.ll.webchattingserver.domain.room.repository.RoomRepository;
import com.ll.webchattingserver.domain.user.User;
import com.ll.webchattingserver.domain.user.UserService;
import com.ll.webchattingserver.room.RoomTestHelper;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class RoomControllerTest {
    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private RoomTestHelper roomTestHelper;
    @Autowired private RoomRepository roomRepository;
    @Autowired private RedisTemplate<String, Object> redisTemplate;
    @Autowired private UserService userService;

    private String userToken1;
    private String userToken2;
    private static final String USERNAME = "홍길동";
    private static final String EMAIL = "test@test.com";
    private static final String PASSWORD = "12345";

    @BeforeEach
    void setUp() throws Exception {
        roomTestHelper.clearRedisData();
        userToken1 = roomTestHelper.getAuthToken(USERNAME, EMAIL, PASSWORD);
        userToken2 = roomTestHelper.getAuthToken(USERNAME + "1", EMAIL + "1", PASSWORD + "1");
    }

    @Nested
    @DisplayName("방 생성")
    class CreateRoom {
        @Test
        @DisplayName("방이 정상적으로 생성되고 저장된다")
        void success() throws Exception {
            // Given
            String roomName = "New Room";

            // When
            UUID roomId = roomTestHelper.createRoom(userToken1, roomName);

            // Then
            // DB 검증
            Room room = roomRepository.findById(roomId).get();
            assertThat(room.getName()).isEqualTo(roomName);
            assertThat(room.getParticipantCount()).isEqualTo(1);

            // Redis 검증
            verifyRoomCacheWithParticipants(List.of(roomId), 1);
        }
    }

    @Nested
    @DisplayName("방 목록 조회")
    class ListRooms {
        @Test
        @DisplayName("전체 방 목록을 조회할 수 있다")
        void listAllRooms() throws Exception {
            // Given
            List<UUID> roomIds = createRooms(userToken1, 2);

            String token2 = roomTestHelper.getAuthToken("김철수", "kim@test.com", PASSWORD);
            roomIds.add(roomTestHelper.createRoom(token2, "Room 3"));

            redisTemplate.delete("chat:rooms");

            // When
            String responseBody = mockMvc.perform(get("/api/room/list")
                            .param("roomName", "Room")
                            .param("page", "0")
                            .param("size", "10")
                            .param("sort", "createdAt,desc")
                            .header("Authorization", "Bearer " + userToken1))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            // Then
            List<RoomRedisDto> rooms = extractRoomList(responseBody);
            assertThat(rooms).hasSize(3);
            assertThat(rooms).extracting("name")
                    .containsExactlyInAnyOrder("Room 1", "Room 2", "Room 3");

            verifyRoomCache(roomIds);
        }

        @Test
        @DisplayName("내 방 목록을 조회할 수 있다")
        void listMyRooms() throws Exception {
            // Given
            List<UUID> roomIds = createRooms(userToken1, 3);

            // When
            String responseBody = mockMvc.perform(get("/api/room/myList")
                            .header("Authorization", "Bearer " + userToken1))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            // Then
            List<RoomRedisDto> myRooms = extractRoomList(responseBody);
            assertThat(myRooms).hasSize(3);
            assertThat(myRooms).extracting("name")
                    .containsExactlyInAnyOrder("Room 1", "Room 2", "Room 3");

            verifyUserRoomCache(USERNAME, roomIds);
        }
    }

    @Nested
    @DisplayName("조회된 방에 참가")
    class JoinRoom{

        @Test
        @DisplayName("남이 만든 방에 참가하면, 내 방 목록에 조회된다.")
        void joinOthersRoom() throws Exception {
            // Given
            List<UUID> roomIds = createRooms(userToken1, 3);

            // When
            for(UUID roomId : roomIds) {
                String joinRoomResponse = mockMvc.perform(post("/api/room/" + roomId + "/join")
                                .header("Authorization", "Bearer " + userToken2))
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString();
            }

            String myRoomsResponse = mockMvc.perform(get("/api/room/myList")
                            .header("Authorization", "Bearer " + userToken2))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            // Then
            List<RoomRedisDto> myRooms = extractRoomList(myRoomsResponse);
            assertThat(myRooms).hasSize(3);
            assertThat(myRooms).extracting("name")
                    .containsExactlyInAnyOrder("Room 1", "Room 2", "Room 3");

            verifyUserRoomCache(USERNAME, roomIds);
        }
    }

    @Nested
    @DisplayName("방에서 영구적으로 나가기")
    class LeaveRoom {

        @Test
        @DisplayName("방에서 나가면, 자신의 목록에서 사라진다.")
        void joinOthersRoom() throws Exception {
            // Given
            List<UUID> roomIds = createRooms(userToken1, 3);

            // When
            for(UUID roomId : roomIds) {
                String joinRoomResponse = mockMvc.perform(post("/api/room/" + roomId + "/leave")
                                .header("Authorization", "Bearer " + userToken1))
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString();
            }

            String myRoomsResponse = mockMvc.perform(get("/api/room/myList")
                            .header("Authorization", "Bearer " + userToken1))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            // Then
            List<RoomRedisDto> myRooms = extractRoomList(myRoomsResponse);
            assertThat(myRooms).hasSize(0);

            User user = userService.findByUsername(USERNAME);
            String userRoomsKey = "chat:user:" + user.getId() + ":rooms";
            Set<Object> cachedRooms = redisTemplate.opsForSet().members(userRoomsKey);
            assertThat(cachedRooms).size().isEqualTo(0);
        }
    }

    private List<UUID> createRooms(String userToken, int count) throws Exception {
        List<UUID> roomIds = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            roomIds.add(roomTestHelper.createRoom(userToken, "Room " + i));
        }
        redisTemplate.delete("chat:user:홍길동:rooms");    // 레디스 정보 삭제
        return roomIds;
    }

    private List<RoomRedisDto> extractRoomList(String responseBody) throws Exception {
        JavaType listType = objectMapper.getTypeFactory().constructParametricType(
                Result.class,
                objectMapper.getTypeFactory().constructCollectionType(List.class, RoomRedisDto.class)
        );
        Result<List<RoomRedisDto>> result = objectMapper.readValue(responseBody, listType);
        return result.getData();
    }

    private void verifyRoomCache(List<UUID> roomIds) {
        for (UUID roomId : roomIds) {
            String roomKey = "chat:room:" + roomId;
            RoomRedisDto cachedRoom = (RoomRedisDto) redisTemplate.opsForValue().get(roomKey);

            assertThat(cachedRoom).isNotNull();
            assertThat(cachedRoom.getId()).isEqualTo(roomId.toString());
        }
    }

    // 참여자 수를 검증해야 하는 경우를 위한 별도 메서드
    private void verifyRoomCacheWithParticipants(List<UUID> roomIds, int expectedParticipants) {
        for (UUID roomId : roomIds) {
            String roomKey = "chat:room:" + roomId;
            RoomRedisDto cachedRoom = (RoomRedisDto) redisTemplate.opsForValue().get(roomKey);

            assertThat(cachedRoom).isNotNull();
            assertThat(cachedRoom.getId()).isEqualTo(roomId.toString());
            assertThat(cachedRoom.getParticipantCount()).isEqualTo(expectedParticipants);
        }
    }

    private void verifyUserRoomCache(String username, List<UUID> roomIds) {
        User user = userService.findByUsername(username);
        String userRoomsKey = "chat:user:" + user.getId() + ":rooms";
        Set<Object> cachedRooms = redisTemplate.opsForSet().members(userRoomsKey);

        assertThat(cachedRooms).isNotNull();
        assertThat(cachedRooms).hasSize(roomIds.size());
        assertThat(cachedRooms.stream()
                .map(obj -> UUID.fromString((String) obj))
                .collect(Collectors.toList()))
                .containsExactlyInAnyOrderElementsOf(roomIds);
    }
}