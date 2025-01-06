package com.ll.webchattingserver.domain.message;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QChat is a Querydsl query type for Chat
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QChat extends EntityPathBase<Chat> {

    private static final long serialVersionUID = -929330249L;

    public static final QChat chat = new QChat("chat");

    public final DateTimePath<java.sql.Timestamp> created_at = createDateTime("created_at", java.sql.Timestamp.class);

    public final StringPath message = createString("message");

    public final StringPath receiveRoom = createString("receiveRoom");

    public final StringPath sender = createString("sender");

    public final NumberPath<Long> TID = createNumber("TID", Long.class);

    public QChat(String variable) {
        super(Chat.class, forVariable(variable));
    }

    public QChat(Path<? extends Chat> path) {
        super(path.getType(), path.getMetadata());
    }

    public QChat(PathMetadata metadata) {
        super(Chat.class, metadata);
    }

}

