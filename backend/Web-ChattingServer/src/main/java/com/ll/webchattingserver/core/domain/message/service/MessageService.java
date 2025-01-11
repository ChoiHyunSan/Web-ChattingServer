package com.ll.webchattingserver.core.domain.message.service;

import com.ll.webchattingserver.core.domain.message.dto.request.MessageRequest;
import com.ll.webchattingserver.core.domain.message.dto.response.MessageResponse;
import com.ll.webchattingserver.entity.chat.Chat;
import com.ll.webchattingserver.entity.chat.repository.ChatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageService {

    private final ChatRepository chatRepository;

    @Transactional
    public void saveChatMessage(MessageRequest msg) {
        Chat chat = Chat.builder().
                sender(msg.getFrom()).
                receiveRoom(msg.getTo()).
                message(msg.getMessage()).
                build();

        chatRepository.save(chat);
    }

    public List<MessageResponse> getChat(LocalDateTime date, String roomId) {
        List<Chat> byDateBetween = chatRepository.findByIdAndDate(roomId, date);
        return byDateBetween.stream().map((chat) -> {
                return MessageResponse.builder()
                    .sender(chat.getSender())
                    .message(chat.getMessage())
                    .createdAt(chat.getCreatedAt())
                    .build();
        }).toList();
    }
}
