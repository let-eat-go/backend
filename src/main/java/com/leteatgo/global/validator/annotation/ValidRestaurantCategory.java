package com.leteatgo.global.validator.annotation;


import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import com.leteatgo.global.validator.RestaurantCategoryValidator;
import jakarta.validation.Constraint;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target({FIELD})
@Retention(RUNTIME)
@Constraint(validatedBy = RestaurantCategoryValidator.class)
public @interface ValidRestaurantCategory {

    String message() default "Invalid restaurant category";

    Class[] groups() default {};

    Class[] payload() default {};
}