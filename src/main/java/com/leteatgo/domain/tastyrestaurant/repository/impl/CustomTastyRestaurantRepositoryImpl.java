package com.leteatgo.domain.tastyrestaurant.repository.impl;

import static com.leteatgo.domain.tastyrestaurant.entity.QTastyRestaurant.tastyRestaurant;
import static com.querydsl.core.types.dsl.MathExpressions.acos;
import static com.querydsl.core.types.dsl.MathExpressions.cos;
import static com.querydsl.core.types.dsl.MathExpressions.radians;
import static com.querydsl.core.types.dsl.MathExpressions.sin;

import com.leteatgo.domain.tastyrestaurant.dto.request.SearchRestaurantsRequest;
import com.leteatgo.domain.tastyrestaurant.entity.TastyRestaurant;
import com.leteatgo.domain.tastyrestaurant.repository.CustomTastyRestaurantRepository;
import com.leteatgo.global.util.OrderByNull;
import com.leteatgo.global.util.SliceUtil;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.annotation.Nullable;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

@RequiredArgsConstructor
public class CustomTastyRestaurantRepositoryImpl implements CustomTastyRestaurantRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<TastyRestaurant> searchRestaurants(SearchRestaurantsRequest request,
            Pageable pageable) {
        NumberExpression<Double> distance = getDistance(request);

        List<TastyRestaurant> contents = queryFactory.selectFrom(tastyRestaurant)
                .where(tastyRestaurant.name.contains(request.keyword()),
                        nearByDistance(request.radius(), distance))
                .orderBy(sortByDistance(request.sort(), distance))
                .fetch();

        return new SliceUtil<>(contents, pageable).getSlice();
    }

    @Nullable
    private NumberExpression<Double> getDistance(SearchRestaurantsRequest request) {
        if (ObjectUtils.isEmpty(request.longitude()) && ObjectUtils.isEmpty(request.latitude())) {
            return null;
        }

        Expression<Double> latitude = Expressions.constant(request.latitude());
        Expression<Double> longitude = Expressions.constant(request.longitude());

        return acos(cos(radians(latitude))
                .multiply(cos(radians(tastyRestaurant.latitude)))
                .multiply(cos(radians(tastyRestaurant.longitude).subtract(radians(longitude))))
                .add(sin(radians(latitude))
                        .multiply(sin(radians(tastyRestaurant.latitude)))))
                .multiply(6371);
    }

    private BooleanExpression nearByDistance(Integer radius, NumberExpression<Double> distance) {
        if (distance == null) {
            return null;
        }

        if (ObjectUtils.isEmpty(radius)) {
            radius = 20000; // max radius
        }

        return distance.loe(radius);
    }

    private OrderSpecifier<Double> sortByDistance(String sort, NumberExpression<Double> distance) {
        if (!StringUtils.hasText(sort) || distance == null) {
            return OrderByNull.DEFAULT;
        }

        if (sort.equals("distance")) {
            return distance.desc();
        }

        return OrderByNull.DEFAULT;
    }
}
