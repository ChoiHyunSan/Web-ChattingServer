package com.ll.webchattingserver.core.domain.room;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.ll.webchattingserver.entity.room.Room;
import com.ll.webchattingserver.entity.userroom.UserRoom;
import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QRoom is a Querydsl query type for Room
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QRoom extends EntityPathBase<Room> {

    private static final long serialVersionUID = 173438529L;

    public static final QRoom room = new QRoom("room");

    public final com.ll.webchattingserver.global.QBaseEntity _super = new com.ll.webchattingserver.global.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final ComparablePath<java.util.UUID> id = createComparable("id", java.util.UUID.class);

    public final StringPath name = createString("name");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final SetPath<UserRoom, com.ll.webchattingserver.core.domain.userroom.QUserRoom> userRooms = this.<UserRoom, com.ll.webchattingserver.core.domain.userroom.QUserRoom>createSet("userRooms", UserRoom.class, com.ll.webchattingserver.core.domain.userroom.QUserRoom.class, PathInits.DIRECT2);

    public QRoom(String variable) {
        super(Room.class, forVariable(variable));
    }

    public QRoom(Path<? extends Room> path) {
        super(path.getType(), path.getMetadata());
    }

    public QRoom(PathMetadata metadata) {
        super(Room.class, metadata);
    }

}

