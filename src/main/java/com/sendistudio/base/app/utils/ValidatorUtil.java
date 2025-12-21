package com.sendistudio.base.app.utils;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;

@Component
public class ValidatorUtil {

    @Autowired
    private Validator validator;

    @Async
    public void validate(Object request) {
        Set<ConstraintViolation<Object>> constraintViolations = validator.validate(request);
        if (constraintViolations.size() > 0) {
            throw new ConstraintViolationException(constraintViolations);
        }
    }
}
