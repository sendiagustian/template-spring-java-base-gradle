package com.sendistudio.base.app.annotations;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Parameters({
    @Parameter(
        name = "page", 
        description = "Page number (starts from 1)", 
        in = ParameterIn.QUERY, 
        schema = @Schema(type = "integer", defaultValue = "1")
    ),
    @Parameter(
        name = "size", 
        description = "Items per page", 
        in = ParameterIn.QUERY, 
        schema = @Schema(type = "integer", defaultValue = "10")
    )
})
public @interface ApiPageParams {
}