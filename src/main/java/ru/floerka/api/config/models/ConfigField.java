package ru.floerka.api.config.models;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(
        ElementType.FIELD
)
@Retention(
        RetentionPolicy.RUNTIME
)
public @interface ConfigField {
    String fieldName() default "";
    boolean isSection() default false;
    String[] comments() default {};
}
