package com.ll.webchattingserver.domain.chat;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long TID;

    @Column
    private String sender;

    @Column
    private String receiveRoom;

    @Column
    private String message;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime created_at;
}
