package com.ll.webchattingserver.global.cache;

import com.ll.webchattingserver.core.domain.room.repository.RoomRepository;
import com.ll.webchattingserver.core.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class RedisSyncScheduler {

    private final RoomRepository roomRepository;
    private final RedisService redisService;
    private final UserService userService;

    @Scheduled(fixedRate = 60000) // 1분마다 실행
    public void syncRedisChangesToDB() {

    }
}
