package com.leteatgo.global.batch.meeting;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.leteatgo.domain.chat.entity.ChatRoom;
import com.leteatgo.domain.chat.event.ChatRoomEventPublisher;
import com.leteatgo.domain.chat.event.dto.CloseChatRoomEvent;
import com.leteatgo.domain.chat.repository.ChatRoomRepository;
import com.leteatgo.domain.chat.type.RoomStatus;
import com.leteatgo.domain.meeting.dto.request.MeetingOptionsRequest;
import com.leteatgo.domain.meeting.entity.Meeting;
import com.leteatgo.domain.meeting.entity.MeetingOptions;
import com.leteatgo.domain.meeting.entity.MeetingParticipant;
import com.leteatgo.domain.meeting.repository.MeetingParticipantRepository;
import com.leteatgo.domain.meeting.repository.MeetingRepository;
import com.leteatgo.domain.meeting.type.AgePreference;
import com.leteatgo.domain.meeting.type.AlcoholPreference;
import com.leteatgo.domain.meeting.type.GenderPreference;
import com.leteatgo.domain.meeting.type.MeetingPurpose;
import com.leteatgo.domain.meeting.type.MeetingStatus;
import com.leteatgo.domain.member.entity.Member;
import com.leteatgo.domain.member.repository.MemberRepository;
import com.leteatgo.domain.member.type.LoginType;
import com.leteatgo.domain.member.type.MemberRole;
import com.leteatgo.domain.region.entity.Region;
import com.leteatgo.domain.region.repository.RegionRepository;
import com.leteatgo.global.type.RestaurantCategory;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.JobRepositoryTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.util.ReflectionTestUtils;

@SpringBatchTest
@SpringBootTest
@ActiveProfiles("test")
@Sql(scripts = "classpath:sql/batch-schema-h2.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class UpdateMeetingsStatusConfigTest {

    @Autowired
    JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    JobRepositoryTestUtils jobRepositoryTestUtils;

    @Autowired
    MeetingRepository meetingRepository;

    @Autowired
    MeetingParticipantRepository meetingParticipantRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    RegionRepository regionRepository;

    @Autowired
    ChatRoomRepository chatRoomRepository;

    @Autowired
    UpdateMeetingsStatusConfig updateMeetingsStatusConfig;

    @MockBean
    ChatRoomEventPublisher chatRoomEventPublisher;

    @Captor
    ArgumentCaptor<CloseChatRoomEvent> closeChatRoomEventCaptor;

    @BeforeEach
    void setUp() {
        saveMeetings();
    }

    @AfterEach
    void tearDown() {
        jobRepositoryTestUtils.removeJobExecutions();
    }


    @Test
    @DisplayName("모임 취소 배치 테스트")
    void cancelMeetingsJobTest() throws Exception {
        // given
        JobParameters jobParameters = new JobParametersBuilder()
                .addDate("date", new Date())
                .toJobParameters();
        // when
        jobLauncherTestUtils.setJob(updateMeetingsStatusConfig.job());
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);
        // then
        assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);

        // 검증: 배치 작업 실행 후 Meeting 상태가 취소로 바뀌었는지 확인
        meetingRepository.findAll().forEach(
                meeting -> assertThat(meeting.getMeetingOptions().getStatus()).isEqualTo(
                        MeetingStatus.CANCELED));
        // 채팅방 삭제 이벤트가 발생했는지 확인
        verify(chatRoomEventPublisher, times(10)).publishCloseChatRoom(
                closeChatRoomEventCaptor.capture());
        List<CloseChatRoomEvent> capturedEvents = closeChatRoomEventCaptor.getAllValues();
        assertThat(capturedEvents).hasSize(10);
    }

    private void saveMeetings() {
        List<Meeting> meetings = new ArrayList<>();
        List<ChatRoom> chatRooms = new ArrayList<>();

        Member mockMember = createTestMember(1L, "test@naver.com", "testnick", "1!qweqwe",
                "01012345678", LoginType.LOCAL, MemberRole.ROLE_USER);
        memberRepository.saveAndFlush(mockMember);

        Region region = new Region("강남구");
        regionRepository.saveAndFlush(region);

        MeetingOptions options = MeetingOptionsRequest.toEntiy(
                new MeetingOptionsRequest(GenderPreference.ANY, AgePreference.ANY,
                        MeetingPurpose.DRINKING, AlcoholPreference.ANY));

        for (int i = 0; i < 10; i++) {
            Meeting meeting = Meeting.builder()
                    .host(mockMember)
                    .name("모임 제목")
                    .restaurantCategory(RestaurantCategory.from("한식"))
                    .region(region)
                    .maxParticipants(4)
                    .minParticipants(2)
                    .startDateTime(LocalDateTime.of(2024, 1, 31, 19, 0))
                    .description("모임 설명")
                    .meetingOptions(options)
                    .build();
            meetings.add(meeting);
        }
        meetingRepository.saveAll(meetings);

        // 모임원 추가
        for (int i = 0; i < 10; i++) {
            Meeting meeting = meetings.get(i);
            meetingParticipantRepository.save(
                    MeetingParticipant.builder().meeting(meeting).member(mockMember).build());
        }

        // 채팅방 생성
        for (int i = 0; i < 10; i++) {
            Meeting meeting = meetings.get(i);
            ChatRoom chatRoom = new ChatRoom(RoomStatus.OPEN, meeting);
            chatRooms.add(chatRoom);
        }
        chatRoomRepository.saveAll(chatRooms);
    }

    private Member createTestMember(Long id, String email, String nickname, String password,
            String phoneNumber, LoginType loginType, MemberRole role) {
        Member member = Member.builder()
                .email(email)
                .nickname(nickname)
                .password(password)
                .phoneNumber(phoneNumber)
                .loginType(loginType)
                .role(role)
                .build();

        ReflectionTestUtils.setField(member, "id", id);

        return member;
    }
}