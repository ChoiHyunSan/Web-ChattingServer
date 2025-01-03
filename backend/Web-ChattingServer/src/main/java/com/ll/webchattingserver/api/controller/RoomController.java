package com.ll.webchattingserver.api.controller;

import com.ll.webchattingserver.api.Result;
import com.ll.webchattingserver.api.dto.redis.RoomRedisDto;
import com.ll.webchattingserver.api.dto.request.room.RoomCreateRequest;
import com.ll.webchattingserver.api.dto.request.room.RoomListRequest;
import com.ll.webchattingserver.api.dto.response.room.RoomCreateResponse;
import com.ll.webchattingserver.api.dto.response.room.RoomJoinResponse;
import com.ll.webchattingserver.api.dto.response.room.RoomListResponse;
import com.ll.webchattingserver.domain.room.RoomCond;
import com.ll.webchattingserver.domain.room.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

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

    @GetMapping("/list")
    public Result<List<RoomRedisDto>> getAllList(
            @RequestParam(value = "roomName", required = false) String roomName,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sort", defaultValue = "createdAt,desc") String sort) {

        RoomListRequest request = RoomListRequest.builder()
                .roomName(roomName)
                .page(page)
                .size(size)
                .sort(sort)
                .build();

        RoomCond cond = RoomCond.of(request.getRoomName(), request.getPage(),
                request.getSize(), request.getSort(), null);
        return Result.success(roomService.getRoomList(cond));
    }

    @GetMapping("/myList")
    public Result<List<RoomRedisDto>> getList(Principal principal){
        return Result.success(roomService.getMyList(principal.getName()));
    }

    @PostMapping("/{roomId}/join")
    public Result<RoomJoinResponse> join(@PathVariable UUID roomId,
                                         Principal principal) {
        return Result.success(roomService.join(principal.getName(), roomId));
    }
}
