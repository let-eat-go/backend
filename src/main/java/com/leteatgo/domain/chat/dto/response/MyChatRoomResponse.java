package com.leteatgo.domain.chat.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.leteatgo.domain.region.entity.Region;
import com.leteatgo.global.type.RestaurantCategory;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MyChatRoomResponse {

    private String meetingName;
    private RestaurantCategory category;
    private Region region;
    private Long roomId;
    private String content;
    private boolean isRead;

    @JsonFormat(pattern = "yyyy-MM-dd mm:ss")
    private LocalDateTime createdAt;
}
