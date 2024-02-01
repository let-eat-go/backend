package com.leteatgo.global.validator;

import com.leteatgo.global.type.RestaurantCategory;
import com.leteatgo.global.validator.annotation.ValidRestaurantCategory;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class RestaurantCategoryValidator implements
        ConstraintValidator<ValidRestaurantCategory, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }

        try {
            RestaurantCategory.from(value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
