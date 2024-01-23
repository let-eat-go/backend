package com.leteatgo.domain.member.entity;

import com.leteatgo.domain.member.type.LoginType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "member",
        uniqueConstraints = {
                @UniqueConstraint(name = "UK_email", columnNames = {"email"}),
                @UniqueConstraint(name = "UK_phone_number", columnNames = {"phone_number"})
        }
)
public class Member {

    public static final String DEFAULT_INTRODUCE = "자기소개를 작성해주세요";
    public static final String DEFAULT_PROFILE_IMAGE = "default_profile_image.png";
    public static final Double DEFAULT_MANNER_TEMPERATURE = 36.5;

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "nickname", nullable = false)
    private String nickname;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Column(name = "profile_image", nullable = false)
    private String profileImage;

    @Column(name = "introduce", nullable = false)
    private String introduce;

    @Column(name = "manner_temperature", nullable = false)
    private Double mannerTemperature;

    @Column(name = "login_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private LoginType loginType;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Builder
    protected Member(String email, String nickname, String password, String phoneNumber,
            LoginType loginType
    ) {
        this.email = email;
        this.nickname = nickname;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.profileImage = DEFAULT_PROFILE_IMAGE;
        this.introduce = DEFAULT_INTRODUCE;
        this.mannerTemperature = DEFAULT_MANNER_TEMPERATURE;
        this.loginType = loginType;
    }
}
