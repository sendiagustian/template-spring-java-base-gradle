package com.sendistudio.base.domain.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class HealthCheckController {

    @GetMapping("/service-check")
    public String help() {
        return "Service is up and running";
    }

}