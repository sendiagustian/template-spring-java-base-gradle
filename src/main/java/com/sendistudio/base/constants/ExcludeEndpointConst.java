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
        
        // Scalar UI & OpenAPI spec
        allExcludes.add("/api/docs");
        allExcludes.add("/api/docs/**");
        allExcludes.add("/v3/api-docs");
        allExcludes.add("/v3/api-docs/**");
        
        // Static resources (served by ApiDocsRedirectController at root)
        allExcludes.add("/favicon.ico");
        allExcludes.add("/favicon.svg");

        // Health check endpoints
        allExcludes.add("/api/service-check");
        allExcludes.add("/api/database-check");
    }

}
