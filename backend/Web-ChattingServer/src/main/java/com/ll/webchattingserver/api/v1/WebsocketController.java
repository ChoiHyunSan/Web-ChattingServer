package com.ll.webchattingserver.api.v1;

import com.ll.webchattingserver.core.domain.chat.dto.request.Message;
import com.ll.webchattingserver.core.domain.chat.service.ChatService;
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
