package com.ll.webchattingserver.entity.room;

import com.ll.webchattingserver.entity.userroom.UserRoom;
import com.ll.webchattingserver.global.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Room extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "room_id")
    private UUID id;
    private String name;

    public static Room of(String roomName) {
        return Room.builder()
                .name(roomName)
                .build();
    }
}
