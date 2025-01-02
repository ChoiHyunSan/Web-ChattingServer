package com.ll.webchattingserver.api.controller;

import com.ll.webchattingserver.api.Result;
import com.ll.webchattingserver.api.dto.request.RoomCreateRequest;
import com.ll.webchattingserver.api.dto.response.RoomCreateResponse;
import com.ll.webchattingserver.domain.room.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/room")
public class RoomController {

    private final RoomService roomService;

    @PostMapping
    public Result<RoomCreateResponse> create(@RequestBody RoomCreateRequest request,
                                             Principal principal){
        return Result.success(roomService.create(principal.getName(), request.getRoomName()));
    }
}
