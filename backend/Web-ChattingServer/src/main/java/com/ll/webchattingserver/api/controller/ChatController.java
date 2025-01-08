package com.ll.webchattingserver.api.controller;

import com.ll.webchattingserver.api.Result;
import com.ll.webchattingserver.api.dto.response.chat.MessageResponse;
import com.ll.webchattingserver.domain.message.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/chat")
public class ChatController {

    private final ChatService chatService;

    @GetMapping("/list/{roomId}/{date}")
    public Result<List<MessageResponse>> list(
            @PathVariable("roomId") String roomId,
            @PathVariable("date")  @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date
    ) {
        return Result.success(chatService.getChat(date.atStartOfDay(), roomId));
    }
}
