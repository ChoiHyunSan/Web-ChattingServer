package com.ll.webchattingserver.domain.room;

import com.ll.webchattingserver.domain.username.UserRoom;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Room {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "room_id")
    private UUID id;

    private String name;

    @CreatedDate
    private LocalDateTime createdAt;

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
