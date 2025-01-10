package com.ll.webchattingserver.domain.user;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.ll.webchattingserver.core.domain.user.User;
import com.ll.webchattingserver.core.domain.user.UserRole;
import com.ll.webchattingserver.core.domain.userroom.UserRoom;
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

    public final com.ll.webchattingserver.global.QBaseEntity _super = new com.ll.webchattingserver.global.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final StringPath email = createString("email");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath password = createString("password");

    public final EnumPath<UserRole> role = createEnum("role", UserRole.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final StringPath username = createString("username");

    public final SetPath<UserRoom, com.ll.webchattingserver.domain.userroom.QUserRoom> userRooms = this.<UserRoom, com.ll.webchattingserver.domain.userroom.QUserRoom>createSet("userRooms", UserRoom.class, com.ll.webchattingserver.domain.userroom.QUserRoom.class, PathInits.DIRECT2);

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

