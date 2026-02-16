package com.sendistudio.base.domain.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sendistudio.base.constants.ScalarTagConst;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api")
@Tag(name = ScalarTagConst.HEALTH_CHECK)
public class HealthCheckController {

    @GetMapping("/service-check")
    public String help() {
        return "Service is up and running";
    }

}