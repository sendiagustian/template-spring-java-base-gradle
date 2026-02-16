package com.sendistudio.base.constants;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
public class ExcludeEndpointConst {

    private List<String> allExcludes = new ArrayList<>();

    public ExcludeEndpointConst() {

        // Scalar api
        allExcludes.add("/scalar/**");
        allExcludes.add("/v3/api-docs/**");
        allExcludes.add("/api-docs/**");
        allExcludes.add("/swagger-ui/**");
        
        // Health Check
        allExcludes.add("/api/service-check");

        // Auth
        allExcludes.add("/api/auth/login");
    }

}
