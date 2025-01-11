package com.ll.webchattingserver.core.domain.message.implement;

import com.ll.webchattingserver.core.domain.message.Message;
import com.ll.webchattingserver.entity.chat.repository.ChatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class MessageReader {

    private final ChatRepository chatRepository;

    public List<Message> findByIdAndDate(String roomId, LocalDateTime date) {
        return chatRepository.findByIdAndDate(roomId, date).stream()
                .map(chat -> {
                    return Message.builder()
                            .to(chat.getReceiveRoom())
                            .from(chat.getSender())
                            .message(chat.getMessage())
                            .time(chat.getCreatedAt())
                            .build();
                }).toList();
    }
}
