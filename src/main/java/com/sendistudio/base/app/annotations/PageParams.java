package com.sendistudio.base.app.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface PageParams {
    int defaultPage() default 1;
    int defaultSize() default 10;
    int maxSize() default 100; 
}