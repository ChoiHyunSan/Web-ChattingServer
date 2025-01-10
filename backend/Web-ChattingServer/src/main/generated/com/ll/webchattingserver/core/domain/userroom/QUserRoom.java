package com.ll.webchattingserver.core.domain.userroom;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUserRoom is a Querydsl query type for UserRoom
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUserRoom extends EntityPathBase<UserRoom> {

    private static final long serialVersionUID = -1402787263L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QUserRoom userRoom = new QUserRoom("userRoom");

    public final com.ll.webchattingserver.global.QBaseEntity _super = new com.ll.webchattingserver.global.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.ll.webchattingserver.core.domain.room.QRoom room;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final com.ll.webchattingserver.core.domain.user.QUser user;

    public QUserRoom(String variable) {
        this(UserRoom.class, forVariable(variable), INITS);
    }

    public QUserRoom(Path<? extends UserRoom> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QUserRoom(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QUserRoom(PathMetadata metadata, PathInits inits) {
        this(UserRoom.class, metadata, inits);
    }

    public QUserRoom(Class<? extends UserRoom> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.room = inits.isInitialized("room") ? new com.ll.webchattingserver.core.domain.room.QRoom(forProperty("room")) : null;
        this.user = inits.isInitialized("user") ? new com.ll.webchattingserver.core.domain.user.QUser(forProperty("user")) : null;
    }

}

