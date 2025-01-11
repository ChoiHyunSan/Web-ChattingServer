package com.ll.webchattingserver.core.domain.message.implement;

import com.ll.webchattingserver.core.domain.message.Message;
import com.ll.webchattingserver.entity.chat.Chat;
import com.ll.webchattingserver.entity.chat.repository.ChatRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessageAppender {

    private final ChatRepository chatRepository;

    public Long save(final Message msg) {
        Chat chat = Chat.builder().
                sender(msg.getFrom()).
                receiveRoom(msg.getTo()).
                message(msg.getMessage()).
                build();
        return chatRepository.save(chat).getId();
    }
}
