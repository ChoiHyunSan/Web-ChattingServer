package com.ll.webchattingserver.domain.intermediate;

import com.ll.webchattingserver.domain.room.Room;
import com.ll.webchattingserver.domain.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class UserRoom {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "userroom_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    Room room;

    @CreatedDate
    private LocalDateTime joinedAt;

    public static UserRoom of(User user, Room room){
        return UserRoom.builder()
                .user(user)
                .room(room)
                .build();
    }
}
