package com.ll.webchattingserver.core.domain.message.service;

import com.ll.webchattingserver.core.domain.message.Message;
import com.ll.webchattingserver.core.domain.message.dto.response.MessageResponse;
import com.ll.webchattingserver.core.domain.message.implement.MessageAppender;
import com.ll.webchattingserver.core.domain.message.implement.MessageReader;
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

    private final MessageAppender messageAppender;
    private final MessageReader messageReader;

    @Transactional
    public void saveChatMessage(Message msg) {
        messageAppender.save(msg);
    }

    public List<MessageResponse> getChat(LocalDateTime date, String roomId) {
        List<Message> byDateBetween = messageReader.findByIdAndDate(roomId, date);
        return byDateBetween.stream().map(MessageResponse::from).toList();
    }
}
