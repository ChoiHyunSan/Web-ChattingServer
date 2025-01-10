package com.ll.webchattingserver.domain.chat.service;

import com.ll.webchattingserver.domain.chat.dto.request.Message;
import com.ll.webchattingserver.domain.chat.dto.response.MessageResponse;
import com.ll.webchattingserver.domain.chat.Chat;
import com.ll.webchattingserver.domain.chat.repository.ChatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;

    @Transactional
    public void saveChatMessage(Message msg) {
        Chat chat = Chat.builder().
                sender(msg.getFrom()).
                receiveRoom(msg.getTo()).
                message(msg.getMessage()).
                build();

        chatRepository.save(chat);
    }

    public List<MessageResponse> getChat(LocalDateTime date, String roomId) {

        log.info("RoomId: {}, date: {}", roomId, date);

        List<Chat> byDateBetween = chatRepository.findByDateBetween(roomId, date);
        return byDateBetween.stream().map((chat) -> {
                return MessageResponse.builder()
                    .sender(chat.getSender())
                    .message(chat.getMessage())
                    .createdAt(chat.getCreatedAt())
                    .build();
        }).toList();
    }
}
