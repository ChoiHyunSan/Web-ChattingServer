package com.ll.webchattingserver.global.cache;

import com.ll.webchattingserver.api.dto.redis.RoomRedisDto;
import com.ll.webchattingserver.domain.room.Room;
import com.ll.webchattingserver.domain.room.RoomRepository;
import com.ll.webchattingserver.domain.user.UserService;
import com.ll.webchattingserver.global.exception.LogicErrorException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class RedisSyncScheduler {

    private final RoomRepository roomRepository;
    private final RedisService redisService;
    private final UserService userService;

    @Scheduled(fixedRate = 60000) // 1분마다 실행
    public void syncRedisChangesToDB() {
        try {
            Set<String> roomKeys = redisService.getRoomkeys();
            roomKeys.forEach(roomKey -> {
                try{
                    syncRoom(roomKey);
                }catch (Exception e){
                    log.error("Failed to sync room {}: {}", roomKey, e.getMessage());
                }
            });

            log.info("Completed Redis to DB sync");
        } catch (Exception e) {
            log.error("Failed to sync Redis changes to DB: {}", e.getMessage());
        }
    }

    private void syncRoom(String key) {
        RoomRedisDto roomData = redisService.getRoom(key)
                .orElseThrow(() -> new LogicErrorException("Room not found in Redis: " + key));

        Set<Long> participantIds = redisService.getParticipants(roomData.getId());

        Room room = roomRepository.findById(roomData.getId())
                .orElseGet(() -> Room.of(roomData.getName()));

        room.updateParticipants(participantIds.stream()
                .map(userService::findById)
                .collect(Collectors.toSet()));

        roomRepository.save(room);
    }
}
