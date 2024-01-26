package com.leteatgo.domain.tastyrestaurant.repository.impl;

import static com.leteatgo.domain.tastyrestaurant.entity.QTastyRestaurant.tastyRestaurant;

import com.leteatgo.domain.tastyrestaurant.entity.TastyRestaurant;
import com.leteatgo.domain.tastyrestaurant.repository.CustomTastyRestaurantRepository;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

@RequiredArgsConstructor
public class CustomTastyRestaurantRepositoryImpl implements CustomTastyRestaurantRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<TastyRestaurant> visitedRestaurants(Integer lastNumOfUses, Pageable pageable) {
        List<TastyRestaurant> contents = queryFactory.selectFrom(tastyRestaurant)
                .where(lessThanNumOfUses(lastNumOfUses))
                .orderBy(tastyRestaurant.numberOfUses.desc())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        boolean hasNext = false;
        if (contents.size() > pageable.getPageSize()) {
            hasNext = true;
            contents.remove(pageable.getPageSize());
        }

        return new SliceImpl<>(contents, pageable, hasNext);
    }

    private BooleanExpression lessThanNumOfUses(Integer lastNumOfUses) {
        if (lastNumOfUses == null) {
            return null;
        }
        return tastyRestaurant.numberOfUses.lt(lastNumOfUses);
    }
}
