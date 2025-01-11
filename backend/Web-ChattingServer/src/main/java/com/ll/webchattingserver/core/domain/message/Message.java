package com.ll.webchattingserver.core.domain.message;

import com.ll.webchattingserver.core.domain.message.dto.request.MessageRequest;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message {
    private String from;
    private String to;
    private String message;
    private LocalDateTime time;

    public static Message of(MessageRequest request) {
        return Message.builder()
                .from(request.getFrom())
                .to(request.getTo())
                .message(request.getMessage())
                .time(request.getTimestamp())
                .build();
    }
}
