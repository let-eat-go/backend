package com.leteatgo.domain.chat.controller;

import com.leteatgo.domain.chat.dto.response.ChatRoomMessagesResponse;
import com.leteatgo.domain.chat.dto.response.MyChatRoomResponse;
import com.leteatgo.domain.chat.service.ChatRoomService;
import com.leteatgo.global.dto.CustomPageRequest;
import com.leteatgo.global.dto.SliceResponse;
import com.leteatgo.global.security.annotation.RoleUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/chat/rooms")
@RequiredArgsConstructor
@RestController
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    /**
     * 채팅방 대화 목록 조회
     *
     * @param roomId  조회할 채팅방 id
     * @param request 페이지 파라미터
     * @return custom slice
     */
    @RoleUser
    @GetMapping("/{roomId}/messages")
    public ResponseEntity<SliceResponse<ChatRoomMessagesResponse>> roomMessages(
            @PathVariable(value = "roomId") Long roomId,
            @Valid CustomPageRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(new SliceResponse<>(
                chatRoomService.roomMessages(roomId, request, userDetails.getUsername())));
    }

    /**
     * 내 채팅방 목록 조회 (최신 메세지 순 정렬)
     *
     * @param request     페이지 파라미터
     * @param userDetails 인증 유저
     * @return custom slice
     */
    @RoleUser
    @GetMapping("/me")
    public ResponseEntity<SliceResponse<MyChatRoomResponse>> myChatRooms(
            @Valid CustomPageRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(new SliceResponse<>(
                chatRoomService.myChatRooms(userDetails.getUsername(), request)));
    }
}
