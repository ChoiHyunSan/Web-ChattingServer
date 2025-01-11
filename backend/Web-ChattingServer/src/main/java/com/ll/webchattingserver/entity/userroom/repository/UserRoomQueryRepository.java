package com.ll.webchattingserver.entity.userroom.repository;

import com.ll.webchattingserver.entity.room.Room;
import com.ll.webchattingserver.entity.user.User;
import com.ll.webchattingserver.entity.userroom.UserRoom;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

import static com.ll.webchattingserver.core.domain.userroom.QUserRoom.userRoom;

@Repository
@RequiredArgsConstructor
public class UserRoomQueryRepository {

    private final JPAQueryFactory queryFactory;

    public List<Room> findRoomsByUserId(Long id) {
        return queryFactory.select(userRoom.room)
                .from(userRoom)
                .where(userRoom.user.id.eq(id))
                .fetch();
    }

    public List<UserRoom> findByUserAndRoom(User user, Room room) {
        return queryFactory.selectFrom(userRoom)
                .join(userRoom.user).fetchJoin()
                .join(userRoom.room).fetchJoin()
                .where(userRoom.user.id.eq(user.getId()).and(userRoom.room.id.eq(room.getId())))
                .fetch();
    }

    public void deleteByRoomIdAndUserId(UUID roomId, Long userId) {
        queryFactory.delete(userRoom)
                .where(userRoom.room.id.eq(roomId).and(userRoom.user.id.eq(userId)))
                .execute();
    }
}
