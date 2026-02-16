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

    public static final String HEALTH_CHECK = "Health Check";
    public static final String AUTHENTICATION = "Authentication";

    public ScalarTagConst() {
        allTags.add(ScalarTagType.builder().order(1).name(HEALTH_CHECK).description("Endpoints for health checks").build());
        allTags.add(ScalarTagType.builder().order(2).name(AUTHENTICATION).description("Endpoints for authentication").build());
    }
}
