package com.leteatgo.domain.tastyrestaurant.entity;

import com.leteatgo.global.entity.BaseEntity;
import com.leteatgo.global.type.RestaurantCategory;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "tasty_restaurant",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_tasty_restaurant_kakao_id", columnNames = {"kakao_id"})
        }
)
public class TastyRestaurant extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "kakao_id", nullable = false)
    private Long kakaoId;

    @Column(name = "name", nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private RestaurantCategory category;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Column(name = "road_address", nullable = false)
    private String roadAddress;

    @Column(name = "land_address", nullable = false)
    private String landAddress;

    @Column(name = "latitude", nullable = false)
    private Double latitude;

    @Column(name = "longitude", nullable = false)
    private Double longitude;

    @Column(name = "restaurant_url", nullable = false)
    private String restaurantUrl;

    @Column(name = "number_of_uses", nullable = false)
    private Integer numberOfUses;
}