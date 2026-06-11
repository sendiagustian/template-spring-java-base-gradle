package com.sendistudio.base.app.utils;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

import com.sendistudio.base.data.responses.WebResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ErrorUtil {
    public WebResponse errorNotFound(DataAccessException e) {
        log.warn("Data not found: {}", e.getMessage());
        return new WebResponse(false, "Data not found");
    }

    public WebResponse errorData(DataAccessException e) {
        log.error("Database error: {}", e.getMessage(), e);
        String safeMessage = "Data error: " + extractSafeMessage(e.getMessage());
        return new WebResponse(false, safeMessage);
    }

    public WebResponse errorServer(Exception e) {
        log.error("Server error: {}", e.getMessage(), e);
        return new WebResponse(false, "Server error: " + e.getMessage());
    }

    public WebResponse errorConnection(Exception e) {
        log.error("Database connection error: {}", e.getMessage(), e);
        return new WebResponse(false, "Database connection failed. Please try again later.");
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
