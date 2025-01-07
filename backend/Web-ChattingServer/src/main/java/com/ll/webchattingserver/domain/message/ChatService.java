package com.ll.webchattingserver.domain.message;

import com.ll.webchattingserver.api.dto.request.chat.Message;
import com.ll.webchattingserver.api.dto.response.chat.MessageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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
                created_at(LocalDateTime.now()).
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
                    .createdAt(chat.getCreated_at())
                    .build();
        }).toList();
    }
}
