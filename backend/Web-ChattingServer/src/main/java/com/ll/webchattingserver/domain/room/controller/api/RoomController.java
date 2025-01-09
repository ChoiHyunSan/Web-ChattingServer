package com.ll.webchattingserver.domain.room.controller.api;

import com.ll.webchattingserver.api.Result;
import com.ll.webchattingserver.domain.room.dto.RoomRedisDto;
import com.ll.webchattingserver.domain.room.dto.request.RoomCreateRequest;
import com.ll.webchattingserver.domain.room.dto.request.RoomListRequest;
import com.ll.webchattingserver.domain.room.dto.response.RoomCreateResponse;
import com.ll.webchattingserver.domain.room.dto.response.RoomJoinResponse;
import com.ll.webchattingserver.domain.room.dto.response.RoomLeaveResponse;
import com.ll.webchattingserver.domain.room.dto.RoomCond;
import com.ll.webchattingserver.domain.room.service.RoomService;
import com.ll.webchattingserver.global.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@Tag(name = "Room API", description = "Room API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/room")
public class RoomController {

    private final RoomService roomService;

    @Operation(
            summary = "새로운 방을 생성합니다.",
            description = "새로운 방 생성"
    )
    @PostMapping
    public Result<RoomCreateResponse> create(
            @RequestBody @Valid RoomCreateRequest request,
            @AuthenticationPrincipal UserPrincipal principal){
        log.info("create: {}", request);
        log.info("principal: {}", principal.getUsername());
        return Result.success(roomService.create(principal.getUsername(), request.getRoomName()));
    }

    @Operation(
            summary = "전체 방 목록을 탐색합니다.",
            description = "전체 방 목록을 탐색"
    )
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

    @Operation(
            summary = "자신이 속한 방 목록을 탐색합니다.",
            description = "자신이 속한 방을 탐색"
    )
    @GetMapping("/myList")
    public Result<List<RoomRedisDto>> getList(
            @AuthenticationPrincipal UserPrincipal principal
    ){
        log.info("User Id: {}, Name : {}", principal.getId(), principal.getUsername());
        return Result.success(roomService.getMyList(principal.getId()));
    }

    @Operation(
            summary = "방에 참여합니다.",
            description = "방에 참여하고 구독 정보를 반환합니다."
    )
    @PostMapping("/{roomId}/join")
    public Result<RoomJoinResponse> join(
            @PathVariable("roomId") UUID roomId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        log.info("User Id: {}, Name : {}", principal.getId(), principal.getUsername());
        return Result.success(roomService.join(principal.getUsername(), roomId));
    }

    @Operation(
            summary = "방에서 나갑니다.",
            description = "참여중인 방에서 나갑니다."
    )
    @PostMapping("/{roomId}/leave")
    public Result<RoomLeaveResponse> leave(
            @PathVariable("roomId") UUID roomId,
            @AuthenticationPrincipal UserPrincipal principal
    ){
        return Result.success(roomService.leave(principal.getId(), roomId));
    }
}
