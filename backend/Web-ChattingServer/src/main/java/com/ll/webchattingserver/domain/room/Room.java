package com.ll.webchattingserver.domain.room;

import com.ll.webchattingserver.domain.user.User;
import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
public class Room {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "room_id")
    private Long id;

    private String name;

    @ManyToMany
    @JoinTable(
        name = "room_participants",
        joinColumns = @JoinColumn(name = "room_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> participants;

    @CreatedDate
    private LocalDateTime created;
}
