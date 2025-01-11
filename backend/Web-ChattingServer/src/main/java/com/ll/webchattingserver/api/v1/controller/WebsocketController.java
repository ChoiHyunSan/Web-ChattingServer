package com.ll.webchattingserver.api.v1.controller;

import com.ll.webchattingserver.core.domain.message.dto.request.MessageRequest;
import com.ll.webchattingserver.core.domain.message.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class WebsocketController {

    private final MessageService chatService;

    @MessageMapping("/chat/message/{roomId}")
    @SendTo("/sub/chat/room/{roomId}")
    public MessageRequest receiveMessage(
            @DestinationVariable("roomId") String roomId,
            MessageRequest message) {

        chatService.saveChatMessage(message);
        return message;
    }
}
