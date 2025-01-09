package com.ll.webchattingserver.domain.room;

import static com.querydsl.core.types.PathMetadataFactory.*;

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

    private static final long serialVersionUID = -1577723274L;

    public static final QRoom room = new QRoom("room");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final ComparablePath<java.util.UUID> id = createComparable("id", java.util.UUID.class);

    public final StringPath name = createString("name");

    public final SetPath<com.ll.webchattingserver.domain.username.UserRoom, com.ll.webchattingserver.domain.username.QUserRoom> userRooms = this.<com.ll.webchattingserver.domain.username.UserRoom, com.ll.webchattingserver.domain.username.QUserRoom>createSet("userRooms", com.ll.webchattingserver.domain.username.UserRoom.class, com.ll.webchattingserver.domain.username.QUserRoom.class, PathInits.DIRECT2);

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

