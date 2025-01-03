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

    }
}
