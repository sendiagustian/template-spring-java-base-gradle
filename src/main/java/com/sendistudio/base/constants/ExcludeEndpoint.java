package com.sendistudio.base.constants;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
public class ExcludeEndpoint {

    private List<String> allExcludes = new ArrayList<>();

    public ExcludeEndpoint() {

        // Health Check
        allExcludes.add("/api/service-check");

        // Auth
        allExcludes.add("/api/auth/login");
    }

}
