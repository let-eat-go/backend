package com.leteatgo.domain.chat.controller;


import static com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leteatgo.domain.chat.dto.response.ChatMessageResponse;
import com.leteatgo.domain.chat.dto.response.ChatMessageResponse.Sender;
import com.leteatgo.domain.chat.dto.response.MyChatRoomResponse;
import com.leteatgo.domain.chat.dto.response.MyChatRoomResponse.Chat;
import com.leteatgo.domain.chat.service.ChatRoomService;
import com.leteatgo.domain.member.entity.Member;
import com.leteatgo.global.dto.CustomPageRequest;
import com.leteatgo.global.security.jwt.JwtAuthenticationFilter;
import com.leteatgo.global.type.RestaurantCategory;
import jakarta.servlet.http.Cookie;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.SliceImpl;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

@WithMockUser(username = "1", roles = "USER")
@AutoConfigureRestDocs
@WebMvcTest(
        controllers = ChatRoomController.class,
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = {
                                JwtAuthenticationFilter.class
                        })
        }
)
class ChatRoomControllerTest {

    @MockBean
    ChatRoomService chatRoomService;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mockMvc;

    static final String URI = "/api/chat/rooms";
    static final String TAG = "ChatRoom";

    @Test
    @DisplayName("채팅방 대화 목록 조회")
    void roomMessages() throws Exception {
        // given
        Long roomId = 1L;
        CustomPageRequest customPageRequest = new CustomPageRequest(1);

        Member member = Member.builder()
                .nickname("nick")
                .profileImage("profile")
                .build();

        ReflectionTestUtils.setField(member, "id", 1L);

        List<ChatMessageResponse> contents = List.of(ChatMessageResponse.builder()
                .sender(Sender.fromEntity(member))
                .content("message 1")
                .isRead(true)
                .createdAt(LocalDateTime.now())
                .build());

        given(chatRoomService.roomMessages(roomId, customPageRequest, "1"))
                .willReturn(new SliceImpl<>(contents,
                        PageRequest.of(customPageRequest.page(), 10), true));

        // when
        // then
        mockMvc.perform(get(URI + "/{roomId}/messages", roomId)
                        .cookie(new Cookie("access_token", "token"))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("채팅방 대화 목록 조회",
                        resource(ResourceSnippetParameters.builder()
                                .tag(TAG)
                                .summary("채팅방 대화 목록 조회")
                                .pathParameters(
                                        parameterWithName("roomId").description("채팅방 id")
                                )
                                .build())
                ));
    }

    @Test
    @DisplayName("내 채팅방 목록 조회")
    void myChatRooms() throws Exception {
        // given
        String authId = "1";
        CustomPageRequest customPageRequest = new CustomPageRequest(1);

        Chat chat = Chat.builder()
                .roomId(1L)
                .content("recent message")
                .isRead(false)
                .build();

        List<MyChatRoomResponse> contents = List.of(MyChatRoomResponse.builder()
                .meetingName("meeting name")
                .category(RestaurantCategory.ASIAN_CUISINE)
                .region("지역")
                .chat(chat)
                .build());

        given(chatRoomService.myChatRooms(authId, customPageRequest))
                .willReturn(new SliceImpl<>(contents,
                        PageRequest.of(customPageRequest.page(), 10), true));

        // when
        // then
        mockMvc.perform(get(URI + "/me")
                        .cookie(new Cookie("access_token", "token"))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("내 채팅방 목록 조회",
                        resource(ResourceSnippetParameters.builder()
                                .tag(TAG)
                                .summary("내 채팅방 목록 조회")
                                .build())
                ));
    }
}