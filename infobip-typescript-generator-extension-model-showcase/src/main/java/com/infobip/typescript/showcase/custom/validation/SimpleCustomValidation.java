package com.infobip.typescript.showcase.custom.validation;

import com.infobip.typescript.CustomTSDecorator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@CustomTSDecorator(decoratorParameterListExtractor = DecoratorParameterListExtractorImpl.class, type = SimpleCustomValidation.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = SimpleCustomValidator.class)
public @interface SimpleCustomValidation {

    String message() default "must be valid element";

    String field();

    int number();

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}