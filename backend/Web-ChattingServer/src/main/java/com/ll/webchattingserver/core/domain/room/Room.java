package com.ll.webchattingserver.core.domain.room;

import com.ll.webchattingserver.core.domain.userroom.UserRoom;
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

    @Builder.Default
    @OneToMany(mappedBy = "room")
    private Set<UserRoom> userRooms = new HashSet<>();

    public static Room of(String roomName) {
        return Room.builder()
                .name(roomName)
                .build();
    }

    public int getParticipantCount() {
        return userRooms.size();
    }
}
