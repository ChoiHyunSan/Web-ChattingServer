package com.ll.webchattingserver.domain.intermediate;

import com.ll.webchattingserver.domain.room.Room;
import com.ll.webchattingserver.domain.user.QUser;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.ll.webchattingserver.domain.intermediate.QUserRoom.userRoom;
import static com.ll.webchattingserver.domain.room.QRoom.room;

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
}
