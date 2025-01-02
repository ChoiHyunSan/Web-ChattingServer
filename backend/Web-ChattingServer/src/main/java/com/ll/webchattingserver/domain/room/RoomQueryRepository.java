package com.ll.webchattingserver.domain.room;

import com.ll.webchattingserver.api.dto.response.room.RoomListResponse;
import com.ll.webchattingserver.domain.user.User;
import com.ll.webchattingserver.domain.user.UserRepository;
import com.ll.webchattingserver.domain.user.UserService;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import static com.ll.webchattingserver.domain.room.QRoom.room;

@Repository
@RequiredArgsConstructor
public class RoomQueryRepository {

    private final JPAQueryFactory queryFactory;

    // 다대다 상황에서, 참여자의 수만 있으면 된다.
    // 이외의 대부분의 상황에서는 Redis의 데이터를 이용할 것이므로, DTO로 가져간다.
    public List<RoomListResponse> findByCond(RoomCond cond) {
        return queryFactory
                .select(Projections.constructor(RoomListResponse.class,
                        room.id,
                        room.name,
                        room.createdAt,
                        room.participants.size()
                ))
                .from(room)
                .where(
                        containUser(cond.getUsername()),
                        equalRoomName(cond.getRoomName())
                )
                .orderBy(getSort(cond.getSort()))
                .offset((long) (cond.getPage() - 1) * cond.getSize())
                .limit(cond.getSize())
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
        return username.map(s -> room.participants.any().username.eq(s)).orElse(null);
    }
}
