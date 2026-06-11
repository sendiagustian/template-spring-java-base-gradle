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
        name = "sortBy", 
        description = "Field name to sort by", 
        in = ParameterIn.QUERY, 
        schema = @Schema(type = "string")
    ),
    @Parameter(
        name = "sortDirection", 
        description = "Sort direction (ASC or DESC)", 
        in = ParameterIn.QUERY, 
        schema = @Schema(type = "string", allowableValues = {"ASC", "DESC"}, defaultValue = "ASC")
    )
})
public @interface ApiSortParams {
}
