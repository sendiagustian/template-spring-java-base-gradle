package com.sendistudio.base.domain.system.services;

import org.springframework.stereotype.Service;

import com.sendistudio.base.data.responses.DataResponse;
import com.sendistudio.base.data.responses.WebResponse;
import com.sendistudio.base.domain.system.sources.HealthCheckSource;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HealthCheckService {
    private final HealthCheckSource healthCheckSource;

    public WebResponse serviceCheck() {
        DataResponse<String> response = new DataResponse<>();
        response.setStatus(true);
        response.setData("Service is up and running");
        response.setMessages("Service is up and running");

        return response;
    }

    public WebResponse databaseCheck() {
        boolean isConnected = healthCheckSource.connectionCheck();

        DataResponse<String> response = new DataResponse<>();
        response.setStatus(isConnected);
        response.setData(isConnected ? "Database is up and running" : "Database is unreachable");
        response.setMessages(isConnected ? "Database is up and running" : "Database is unreachable");

        return response;
    }
}
