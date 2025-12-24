package com.sendistudio.base.app.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.circuitbreaker.event.CircuitBreakerOnStateTransitionEvent;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class CircuitBreakerConfig {

    @Autowired
    private CircuitBreakerRegistry circuitBreakerRegistry;

    @PostConstruct
    public void init() {
        circuitBreakerRegistry.getAllCircuitBreakers().forEach(circuitBreaker -> {
            circuitBreaker.getEventPublisher()
                    .onStateTransition(event -> {
                        CircuitBreakerOnStateTransitionEvent stateEvent = (CircuitBreakerOnStateTransitionEvent) event;
                        log.warn("Circuit Breaker [{}] state changed from {} to {}",
                                event.getCircuitBreakerName(),
                                stateEvent.getStateTransition().getFromState(),
                                stateEvent.getStateTransition().getToState());
                    })
                    .onError(event -> {
                        log.error("Circuit Breaker [{}] recorded error: {}",
                                event.getCircuitBreakerName(),
                                event.getThrowable().getMessage());
                    })
                    .onSuccess(event -> {
                        log.info("Circuit Breaker [{}] recorded success",
                                event.getCircuitBreakerName());
                    })
                    .onCallNotPermitted(event -> {
                        log.warn("Circuit Breaker [{}] call not permitted - circuit is OPEN",
                                event.getCircuitBreakerName());
                    });

            log.info("Circuit Breaker event listeners initialized for [{}]", circuitBreaker.getName());
        });
    }
}
