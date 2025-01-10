package com.ll.webchattingserver.core.domain.chat.dto.request;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Message {
    private String from;
    private String to;
    private String message;
    private LocalDateTime timestamp;
}
