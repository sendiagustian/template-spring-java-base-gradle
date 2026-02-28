package com.sendistudio.base.constants;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.sendistudio.base.constants.types.ScalarTagType;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
public class ScalarTagConst {
    
    private List<ScalarTagType> allTags = new ArrayList<>();

    // API Docs
    public static final String API_DOCS = "API Docs";
    // Health Check
    public static final String HEALTH_CHECK = "Health Check";
    // Authentication
    public static final String AUTHENTICATION = "Authentication";

    public ScalarTagConst() {
        allTags.add(ScalarTagType.builder().order(1).name(API_DOCS).description("Endpoints for API documentation").build());
        allTags.add(ScalarTagType.builder().order(2).name(HEALTH_CHECK).description("Endpoints for health checks").build());
        allTags.add(ScalarTagType.builder().order(3).name(AUTHENTICATION).description("Endpoints for authentication").build());
    }
}
