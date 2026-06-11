package com.sendistudio.base.app.handlers;

import com.sendistudio.base.app.handlers.exceptions.BadRequestException;
import com.sendistudio.base.app.handlers.exceptions.ConflictException;
import com.sendistudio.base.app.handlers.exceptions.EmailServiceException;
import com.sendistudio.base.app.handlers.exceptions.ForbiddenException;
import com.sendistudio.base.app.handlers.exceptions.InternalServerException;
import com.sendistudio.base.app.handlers.exceptions.ResourceNotFoundException;
import com.sendistudio.base.app.utils.ErrorUtil;
import com.sendistudio.base.data.responses.WebResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.async.AsyncRequestNotUsableException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.net.SocketTimeoutException;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final ErrorUtil errorUtil;

    // --- SECURITY HANDLERS ---

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<WebResponse> handleBadCredentials(BadCredentialsException ex) {
        return buildWebResponse(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<WebResponse> handleAuthException(AuthenticationException ex) {
        return buildWebResponse(HttpStatus.UNAUTHORIZED, "Authentication failed: " + ex.getMessage());
    }

    @ExceptionHandler(InternalAuthenticationServiceException.class)
    public ResponseEntity<WebResponse> handleInternalAuth(InternalAuthenticationServiceException ex) {
        // Cek apakah penyebabnya adalah BadCredentials?
        if (ex.getCause() instanceof BadCredentialsException) {
            return buildWebResponse(HttpStatus.UNAUTHORIZED, ex.getCause().getMessage());
        }
        // Jika bukan, berarti error lain (misal DB mati saat login)
        log.error("Authentication Internal Error", ex);
        return buildWebResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Authentication system error.");
    }

    // --- DATABASE HANDLERS (Level Up) ---

    // 1. Handle Duplikat / Foreign Key Error (Ini yang return 409/400)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<WebResponse> handleDataIntegrity(DataIntegrityViolationException ex) {
        // Ambil pesan root cause paling dalam
        String rootMsg = ex.getRootCause() != null ? ex.getRootCause().getMessage() : ex.getMessage();
        String safeMessage = errorUtil.extractSafeMessage(rootMsg);

        log.warn("Data Integrity Error: {}", safeMessage);
        return buildWebResponse(HttpStatus.CONFLICT, safeMessage);
    }

    // 2. Handle Koneksi Mati (Ini yang return 503)
    @ExceptionHandler(CannotGetJdbcConnectionException.class)
    public ResponseEntity<WebResponse> handleDbConnection(CannotGetJdbcConnectionException ex) {
        log.error("Database Down: ", ex);

        return buildWebResponse(HttpStatus.SERVICE_UNAVAILABLE,
                "Database connection failed. Please try again later.");
    }

    // 3. Handle Error DB Umum Lainnya (Sisa-sisa error query)
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<WebResponse> handleGeneralDb(DataAccessException ex) {
        log.error("Database General Error: ", ex);
        return buildWebResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Database processing error.");
    }

    // 4. Handle conflict error yang dilempar manual dari service
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<WebResponse> handleConflict(ConflictException ex) {
        log.warn("Conflict: {}", ex.getMessage());
        return buildWebResponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    // 5. Menangkap error otomatis dari JdbcTemplate (queryForObject kosong)
    @ExceptionHandler(EmptyResultDataAccessException.class)
    public ResponseEntity<WebResponse> handleEmptyResult(EmptyResultDataAccessException ex) {
        log.warn("Data not found in database: {}", ex.getMessage());
        return buildWebResponse(HttpStatus.NOT_FOUND, "The requested data was not found.");
    }

    // 6. Menangkap error manual yang kamu lempar dari Service
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<WebResponse> handleResourceNotFound(ResourceNotFoundException ex) {
        return buildWebResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    // --- VALIDATION HANDLERS ---

    // Handle Error Validasi @Valid / @NotBlank di DTO Request
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<WebResponse> handleValidation(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.joining(", "));

        return buildWebResponse(HttpStatus.BAD_REQUEST, "Validation failed: " + errorMessage);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<WebResponse> handleMissingParams(MissingServletRequestParameterException ex) {
        return buildWebResponse(HttpStatus.BAD_REQUEST, "Parameter '" + ex.getParameterName() + "' is required.");
    }

    // --- STANDARD HTTP HANDLERS ---

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<WebResponse> handleNotFound(NoHandlerFoundException ex) {
        return buildWebResponse(HttpStatus.NOT_FOUND, "Endpoint not found: " + ex.getRequestURL());
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<WebResponse> handleNoResourceFound(NoResourceFoundException ex) {
        return buildWebResponse(HttpStatus.NOT_FOUND, "Endpoint not found: " + ex.getMessage());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<WebResponse> handleMethodNotAllowed(HttpRequestMethodNotSupportedException ex) {
        return buildWebResponse(HttpStatus.METHOD_NOT_ALLOWED, "Method " + ex.getMethod() + " not allowed.");
    }

    @ExceptionHandler(SocketTimeoutException.class)
    public ResponseEntity<WebResponse> handleTimeout(SocketTimeoutException ex) {
        return buildWebResponse(HttpStatus.REQUEST_TIMEOUT, "Request timed out.");
    }
    
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<WebResponse> handleForbidden(ForbiddenException ex) {
        return buildWebResponse(HttpStatus.FORBIDDEN, ex.getMessage());
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<WebResponse> handleBadRequest(BadRequestException ex) {
        return buildWebResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    // --- Handle Email Service Errors ---
    @ExceptionHandler(EmailServiceException.class)
    public ResponseEntity<WebResponse> handleEmailError(EmailServiceException ex) {
        log.error("Email Service Error: ", ex);
        return buildWebResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Email service error: " + ex.getMessage());
    }

    // --- GLOBAL FALLBACK ---

    // Client disconnected before response was fully written (e.g. browser closed tab while
    // Scalar JS was streaming). There is nothing to write back, so return null.
    @ExceptionHandler(AsyncRequestNotUsableException.class)
    public ResponseEntity<WebResponse> handleAsyncNotUsable(AsyncRequestNotUsableException ex) {
        log.debug("[GlobalExceptionHandler] Client disconnected mid-response: {}", ex.getMessage());
        return null;
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<WebResponse> handleGlobal(Exception ex) {
        log.error("Unhandled Exception: ", ex);
        // Jangan tampilkan ex.getMessage() mentah ke user di production (security risk)
        WebResponse response = new WebResponse(false, "Internal Server Error. Please contact administrator.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(InternalServerException.class)
    public ResponseEntity<WebResponse> handleInternalServer(InternalServerException ex) {
        log.error("Internal Server Error: {}", ex.getMessage());
        return buildWebResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }

    // --- PRIVATE HELPER (Using your WebResponse class) ---

    private ResponseEntity<WebResponse> buildWebResponse(HttpStatus status, String message) {
        // Kita pakai constructor WebResponse(Boolean status, String messages)
        WebResponse WebResponse = new WebResponse(false, message);
        return ResponseEntity.status(status).body(WebResponse);
    }
}