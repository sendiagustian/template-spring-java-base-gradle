package com.sendistudio.base.domain.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sendistudio.base.data.responses.ErrorResponse;
import com.sendistudio.base.data.responses.WebResponse;
import com.sendistudio.base.domain.services.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping("/all")
    public ResponseEntity<WebResponse> getAllUsers() {
        WebResponse response = userService.getAll();

        // Check if response is successful
        if (response.getStatus()) {
            return ResponseEntity.ok(response);
        }

        // Handle error responses
        if (response instanceof ErrorResponse) {
            ErrorResponse errorResponse = (ErrorResponse) response;
            String message = errorResponse.getMessages();

            // Service unavailable (circuit breaker open)
            if (message != null && message.contains("temporarily unavailable")) {
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
            }

            // Not found
            if (message != null && message.contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            // Server error (default for other errors)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/contacts")
    public ResponseEntity<WebResponse> getAllUserContactss() {
        WebResponse response = userService.getUserContact(
                "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJzZW5kaWFndXN0aWFuIn0.R1ojFb6Xx6Su6lbl9xOoqCUWXgcu6bvL__QWK2GsSk0",
                "test");

        // Check if response is successful
        if (response.getStatus()) {
            return ResponseEntity.ok(response);
        }

        // Handle error responses
        if (response instanceof ErrorResponse) {
            ErrorResponse errorResponse = (ErrorResponse) response;
            String message = errorResponse.getMessages();

            // Service unavailable (circuit breaker open)
            if (message != null && message.contains("temporarily unavailable")) {
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
            }

            // Not found
            if (message != null && message.contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            // Server error (default for other errors)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

        return ResponseEntity.ok(response);
    }
}