package com.sendistudio.base.app.utils;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

import com.sendistudio.base.data.responses.ErrorResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ErrorUtil {
    public ErrorResponse errorNotFound(DataAccessException e) {
        log.info("Data not found Error : " + e.getMessage());
        return new ErrorResponse(false, "Data not found");
    }

    public ErrorResponse errorData(DataAccessException e) {
        log.info("Data Error : " + e.getMessage());
        String safeMessage = "Data error: " + extractSafeMessage(e.getMessage());
        return new ErrorResponse(false, safeMessage);
    }

    public ErrorResponse errorServer(Exception e) {
        log.info("Server Error : " + e.getMessage());
        return new ErrorResponse(false, "Server error: " + e.getMessage());
    }

    private String extractSafeMessage(String message) {
        if (message.contains("Duplicate entry")) {
            return "Duplicate data entry.";
        } else if (message.contains("bad SQL grammar")) {
            return "Query grammer invalid operation.";
        } else if (message.contains("Parameter index out of range")) {
            return "Invalid parameters for data operation.";
        } else if (message.contains("cannot be null")) {
            return "Invalid data: Required field is missing.";
        } else if (message.contains("Data truncated")) {
            return "Invalid data: Field value is out of range or invalid.";
        } else if (message.contains("foreign key constraint fails")) {
            return "Invalid reference: Related data is missing or invalid.";
        }

        return message;
    }
}
