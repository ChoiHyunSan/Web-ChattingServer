package com.ll.webchattingserver.domain.user;

import jakarta.persistence.*;
import lombok.*;

/**
 * 테스트 용이성을 위해 우선 아이디/이메일 중복 등 기본적인 제약만 걸어둠 (추후에 수정 예정)
 */

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
public class User {

    @Id @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;

    private String password;

    @Column(unique = true)
    private String email;

    public static User of(String username, String password, String email) {
        return User.builder()
                .username(username)
                .password(password)
                .email(email)
                .build();
    }
}