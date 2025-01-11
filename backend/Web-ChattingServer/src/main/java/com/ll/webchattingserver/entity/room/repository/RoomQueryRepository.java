package com.ll.webchattingserver.entity.room.repository;

import com.ll.webchattingserver.core.domain.room.dto.RoomCond;
import com.ll.webchattingserver.entity.room.Room;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

import static com.ll.webchattingserver.entity.room.QRoom.room;
import static com.ll.webchattingserver.entity.userroom.QUserRoom.userRoom;

@Repository
@RequiredArgsConstructor
public class RoomQueryRepository {

    private final JPAQueryFactory queryFactory;

    // 다대다 상황에서, 참여자의 수만 있으면 된다.
    // 이외의 대부분의 상황에서는 Redis의 데이터를 이용할 것이므로, DTO로 가져간다.
    public List<Room> findByCond(RoomCond cond) {
        return queryFactory
                .selectFrom(room)
                .where(
                        containUser(cond.getUsernameOpt()),
                        equalRoomName(cond.getRoomNameOpt())
                )
                .orderBy(getSort(cond.getSort()))
                .offset(Math.max(0, (cond.getPage() - 1) * cond.getSize()))  // offset이 음수가 되지 않도록 보정
                .limit(cond.getSize())
                .fetch();
    }

    public List<Room> findRoomsByUserId(Long id) {
        return queryFactory.selectFrom(room)
                .from(room)
                .leftJoin(userRoom).on(room.id.eq(userRoom.roomId))
                .where(userRoom.userId.eq(id))
                .fetch();
    }

    private OrderSpecifier<?> getSort(String sort) {
        if (sort == null) return room.createdAt.desc();
        return switch (sort.toLowerCase()){
            case "name" -> room.name.asc();
            case "id" -> room.id.asc();
            default -> room.createdAt.desc();
        };
    }

    private BooleanExpression equalRoomName(Optional<String> roomNameOpt) {
        if(roomNameOpt.isEmpty()) return null;

        String roomName = roomNameOpt.get();
        return roomName.trim().isEmpty() ? room.name.isNotNull() : room.name.like("%" + roomName + "%");
    }

    private BooleanExpression containUser(Optional<String> username) {
        return null;
        // return username.map(userRoom.user.username::eq).orElse(null);
    }
}
