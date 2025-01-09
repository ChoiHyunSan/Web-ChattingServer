package com.ll.webchattingserver.domain.user;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUser is a Querydsl query type for User
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUser extends EntityPathBase<User> {

    private static final long serialVersionUID = -170708874L;

    public static final QUser user = new QUser("user");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final StringPath email = createString("email");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath password = createString("password");

    public final EnumPath<UserRole> role = createEnum("role", UserRole.class);

    public final StringPath username = createString("username");

    public final SetPath<com.ll.webchattingserver.domain.userroom.UserRoom, com.ll.webchattingserver.domain.userroom.QUserRoom> userRooms = this.<com.ll.webchattingserver.domain.userroom.UserRoom, com.ll.webchattingserver.domain.userroom.QUserRoom>createSet("userRooms", com.ll.webchattingserver.domain.userroom.UserRoom.class, com.ll.webchattingserver.domain.userroom.QUserRoom.class, PathInits.DIRECT2);

    public QUser(String variable) {
        super(User.class, forVariable(variable));
    }

    public QUser(Path<? extends User> path) {
        super(path.getType(), path.getMetadata());
    }

    public QUser(PathMetadata metadata) {
        super(User.class, metadata);
    }

}

