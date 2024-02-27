package com.leteatgo.domain.tastyrestaurant.entity;

import com.leteatgo.global.entity.BaseEntity;
import com.leteatgo.global.type.RestaurantCategory;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "tasty_restaurant",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_tasty_restaurant_api_id", columnNames = {"api_id"})
        },
        indexes = @Index(name = "idx_tasty_restaurant_api_id", columnList = "api_id")
)
public class TastyRestaurant extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "api_id", nullable = false)
    private Long apiId;

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

    @Builder
    public TastyRestaurant(Long apiId, String name, RestaurantCategory category,
            String phoneNumber, String roadAddress, String landAddress, Double latitude,
            Double longitude, String restaurantUrl, Integer numberOfUses) {
        this.apiId = apiId;
        this.name = name;
        this.category = category;
        this.phoneNumber = phoneNumber;
        this.roadAddress = roadAddress;
        this.landAddress = landAddress;
        this.latitude = latitude;
        this.longitude = longitude;
        this.restaurantUrl = restaurantUrl;
        this.numberOfUses = numberOfUses;
    }

    public void increaseNumberOfUses() {
        this.numberOfUses++;
    }
}