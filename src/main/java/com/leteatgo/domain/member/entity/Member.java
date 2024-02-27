package com.leteatgo.domain.member.entity;

import com.leteatgo.domain.member.type.LoginType;
import com.leteatgo.domain.member.type.MemberRole;
import com.leteatgo.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

@SQLRestriction("deleted_at is null")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "member",
        uniqueConstraints = {
                @UniqueConstraint(name = "UK_email", columnNames = {"email"}),
        }
)
public class Member extends BaseEntity {

    public static final String DEFAULT_INTRODUCE = "자기소개를 작성해주세요";
    public static final Double DEFAULT_MANNER_TEMPERATURE = 36.5;
    public static final String DEFAULT_PASSWORD = "1!qweqwe";
    public static final String DEFAULT_PHONE_NUMBER = "소셜로그인 사용자";

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

    @Column(name = "profile_image")
    private String profileImage;

    @Column(name = "profile_filename")
    private String profileFilename;

    @Column(name = "introduce", nullable = false)
    private String introduce;

    @Column(name = "manner_temperature", nullable = false)
    private Double mannerTemperature = DEFAULT_MANNER_TEMPERATURE;

    @Column(name = "login_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private LoginType loginType;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private MemberRole role;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Builder
    protected Member(String email, String nickname, String password, String phoneNumber,
            String profileImage, LoginType loginType, MemberRole role, String introduce
    ) {
        this.email = email;
        this.nickname = nickname;
        this.password = password == null ? DEFAULT_PASSWORD : password;
        this.phoneNumber = phoneNumber == null ? DEFAULT_PHONE_NUMBER : phoneNumber;
        this.profileImage = profileImage;
        this.introduce = introduce == null ? DEFAULT_INTRODUCE : introduce;
        this.loginType = loginType;
        this.role = role;
    }

    public void decreaseMannerTemperature(double mannerTemperature) {
        this.mannerTemperature -= mannerTemperature;
    }

    public void addProfile(String url, String filename) {
        this.profileImage = url;
        this.profileFilename = filename;
    }

    public void updateInfo(String nickname, String introduce) {
        this.nickname = nickname;
        this.introduce = introduce;
    }

    public void setDeletedAt(LocalDateTime now) {
        deletedAt = now;
    }

    public void updateMannerTemperature(Double score) {
        this.mannerTemperature -= -1 * score;
    }
}
