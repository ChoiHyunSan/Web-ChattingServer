package com.ll.webchattingserver.core.domain.room.dto;

import lombok.*;

import java.util.Optional;

@Data
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
public class RoomCond {
    private String roomName;
    private int page;
    private int size;
    private String sort;
    private String username;

    public static RoomCond of(String roomName, Integer page, Integer size, String sort, String username) {
        return RoomCond.builder()
                .roomName(roomName)
                .page(page)
                .size(size)
                .sort(sort)
                .username(username)
                .build();
    }

    public Optional<String> getRoomNameOpt() {
        return Optional.ofNullable(roomName);
    }

    public Optional<String> getUsernameOpt() {
        return Optional.ofNullable(username);
    }
}
