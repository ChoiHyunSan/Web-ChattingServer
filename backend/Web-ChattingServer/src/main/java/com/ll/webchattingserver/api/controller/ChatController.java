package com.ll.webchattingserver.api.controller;

import com.ll.webchattingserver.api.dto.request.chat.Message;
import com.ll.webchattingserver.domain.message.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final ChatService chatService;

    @MessageMapping("/chat/message/{roomId}")
    @SendTo("/sub/chat/room/{roomId}")
    public Message receiveMessage(
            @DestinationVariable("roomId") String roomId,
            Message message) {

        chatService.saveChatMessage(message);
        return message;
    }
}
