package com.ll.webchattingserver.core.domain.message.dto.response;

import com.ll.webchattingserver.core.domain.message.Message;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageResponse {
    private String sender;
    private String message;
    private LocalDateTime createdAt;

    public static MessageResponse from(Message message) {
        return MessageResponse.builder()
                .sender(message.getTo())
                .message(message.getMessage())
                .createdAt(message.getTime())
                .build();
    }
}
