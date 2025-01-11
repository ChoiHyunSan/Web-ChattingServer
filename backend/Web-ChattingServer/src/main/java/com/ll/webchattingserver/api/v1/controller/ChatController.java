package com.ll.webchattingserver.api.v1.controller;

import com.ll.webchattingserver.api.v1.Result;
import com.ll.webchattingserver.core.domain.message.dto.response.MessageResponse;
import com.ll.webchattingserver.core.domain.message.service.MessageService;
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

    private final MessageService chatService;

    @GetMapping("/list/{roomId}/{date}")
    public Result<List<MessageResponse>> list(
            @PathVariable("roomId") String roomId,
            @PathVariable("date")  @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date
    ) {
        return Result.success(chatService.getChat(date.atStartOfDay(), roomId));
    }
}
