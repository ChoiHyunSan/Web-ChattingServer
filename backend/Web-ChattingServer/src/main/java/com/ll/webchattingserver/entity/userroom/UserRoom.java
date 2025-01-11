package com.ll.webchattingserver.entity.userroom;

import com.ll.webchattingserver.entity.room.Room;
import com.ll.webchattingserver.entity.user.User;
import com.ll.webchattingserver.global.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class UserRoom extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "userroom_id")
    private Long id;

    private Long userId;
    private UUID roomId;

    public static UserRoom of(Long userId, UUID roomId) {
        return UserRoom.builder()
                .userId(userId)
                .roomId(roomId)
                .build();
    }
}
