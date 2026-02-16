package com.sendistudio.base.app.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Parameters({
    @Parameter(
        name = "X-API-TOKEN",
        description = "JWT or API token",
        in = ParameterIn.HEADER,
        required = true,
        schema = @Schema(type = "string", example = "eyJhbGci...")
    )
})
public @interface ApiTokenHeader {
}
