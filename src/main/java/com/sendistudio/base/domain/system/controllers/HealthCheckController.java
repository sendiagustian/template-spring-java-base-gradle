package com.sendistudio.base.domain.system.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sendistudio.base.constants.ScalarTagConst;
import com.sendistudio.base.data.responses.WebResponse;
import com.sendistudio.base.domain.system.services.HealthCheckService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = ScalarTagConst.HEALTH_CHECK, description = "Endpoints for health check and service status")
public class HealthCheckController {

    private final HealthCheckService healthCheckService;

    @GetMapping("/service-check")
    public ResponseEntity<WebResponse> serviceCheck() {
        return ResponseEntity.ok(healthCheckService.serviceCheck());
    }

    @GetMapping("/database-check")
    public ResponseEntity<WebResponse> databaseCheck() {
        WebResponse response = healthCheckService.databaseCheck();
        HttpStatus status = Boolean.TRUE.equals(response.getStatus())
                ? HttpStatus.OK
                : HttpStatus.SERVICE_UNAVAILABLE;
        return ResponseEntity.status(status).body(response);
    }

}