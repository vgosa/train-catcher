package org.group21.payment.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import org.group21.payment.validator.EnumValueValidator;

import java.lang.annotation.*;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = EnumValueValidator.class)
/**
 * Validate a String value against the possible values of an Enum class.
 */
public @interface EnumValue {
    Class<? extends Enum<?>> enumClass();
    String message() default "Must be any value of enum {enumClass}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
