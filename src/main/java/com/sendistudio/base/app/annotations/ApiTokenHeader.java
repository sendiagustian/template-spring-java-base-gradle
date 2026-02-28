package com.sendistudio.base.app.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@SecurityRequirement(name = "X-API-TOKEN")
public @interface ApiTokenHeader {
}
