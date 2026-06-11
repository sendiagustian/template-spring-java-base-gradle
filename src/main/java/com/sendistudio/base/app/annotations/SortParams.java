package com.sendistudio.base.app.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface SortParams {
    /**
     * Default field to sort by if not specified in query params
     */
    String defaultSortBy() default "";
    
    /**
     * Default sort direction (ASC or DESC)
     */
    String defaultDirection() default "ASC";
    
    /**
     * Allowed fields for sorting (security whitelist to prevent SQL injection)
     * Empty array means no validation (use with caution)
     */
    String[] allowedFields() default {};
}
