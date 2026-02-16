package com.sendistudio.base.app.utils;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

import com.sendistudio.base.data.responses.global.ErrorResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ErrorUtil {
    public ErrorResponse errorNotFound(DataAccessException e) {
        log.warn("Data not found: {}", e.getMessage());
        return new ErrorResponse(false, "Data not found");
    }

    public ErrorResponse errorData(DataAccessException e) {
        log.error("Database error: {}", e.getMessage(), e);
        String safeMessage = "Data error: " + extractSafeMessage(e.getMessage());
        return new ErrorResponse(false, safeMessage);
    }

    public ErrorResponse errorServer(Exception e) {
        log.error("Server error: {}", e.getMessage(), e);
        return new ErrorResponse(false, "Server error: " + e.getMessage());
    }

    public ErrorResponse errorConnection(Exception e) {
        log.error("Database connection error: {}", e.getMessage(), e);
        return new ErrorResponse(false, "Database connection failed. Please try again later.");
    }

    public String extractSafeMessage(String rawMessage) {
        if (rawMessage == null) return "Unknown database error";

        if (rawMessage.contains("duplicate key value")) {
            return "Data already exists (Duplicate Entry).";
        } else if (rawMessage.contains("bad SQL grammar")) {
            return "System Error: Invalid Query Operation.";
        } else if (rawMessage.contains("Parameter index out of range")) {
            return "System Error: Invalid parameters.";
        } else if (rawMessage.contains("cannot be null")) {
            return "Invalid data: Required field is missing.";
        } else if (rawMessage.contains("Data truncated")) {
            return "Invalid data: Input too long or invalid format.";
        } else if (rawMessage.contains("foreign key constraint fails") || rawMessage.contains("violates foreign key constraint")) {
            return "Invalid reference: Related data does not exist or is currently used.";
        }
        
        // Log pesan aslinya untuk developer (biar bisa debug)
        log.warn("Unhandled SQL Error Message: {}", rawMessage);
        
        return "Database operation failed.";
    }
}
