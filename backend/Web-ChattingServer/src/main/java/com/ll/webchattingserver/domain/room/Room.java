package com.ll.webchattingserver.domain.room;

import com.ll.webchattingserver.domain.user.User;
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

    @Builder.Default
    @ManyToMany
    @JoinTable(
        name = "room_participants",
        joinColumns = @JoinColumn(name = "room_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> participants = new HashSet<>();

    @CreatedDate
    private LocalDateTime createdAt;

    public static Room of(String roomName) {
        return Room.builder()
                .name(roomName)
                .build();
    }

    public void join(User user) {
        participants.add(user);
    }

    public void leave(User user) {
        this.participants.remove(user);
    }

    public void updateParticipants(Set<User> participants) {
        this.participants = participants;
    }
}
