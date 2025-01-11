package com.ll.webchattingserver.entity.userroom.repository;

import com.ll.webchattingserver.entity.room.Room;
import com.ll.webchattingserver.entity.userroom.UserRoom;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

import static com.ll.webchattingserver.entity.userroom.QUserRoom.userRoom;


@Repository
@RequiredArgsConstructor
public class UserRoomQueryRepository {

    private final JPAQueryFactory queryFactory;

    public List<UserRoom> findByUserAndRoom(Long userId, UUID roomId) {
        return queryFactory.selectFrom(userRoom)
                .where(userRoom.userId.eq(userId).and(userRoom.roomId.eq(roomId)))
                .fetch();
    }

    public void deleteByRoomIdAndUserId(UUID roomId, Long userId) {
        queryFactory.delete(userRoom)
                .where(userRoom.userId.eq(userId).and(userRoom.roomId.eq(roomId)))
                .execute();
    }
}
