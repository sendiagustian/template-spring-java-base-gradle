package com.sendistudio.base.app.middlewares;

import java.net.SocketTimeoutException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.NoHandlerFoundException;

import com.sendistudio.base.app.utils.ErrorUtil;
import com.sendistudio.base.data.responses.ErrorResponse;

import jakarta.validation.ConstraintViolationException;

@ControllerAdvice
public class ErrorMiddleware {

    @Autowired
    private ErrorUtil errorUtil;

    // --- SECURITY HANDLERS ---

    // Handle Login Gagal (Password Salah) -> Biar jadi 401, bukan 500
    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException ex) {
        return new ResponseEntity<>(
                new ErrorResponse(false, "Username atau Password salah"),
                HttpStatus.UNAUTHORIZED);
    }

    // Handle Unauthorized lainnya (Token invalid, dll)
    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException ex) {
        return new ResponseEntity<>(
                new ErrorResponse(false, "Unauthorized: " + ex.getMessage()),
                HttpStatus.UNAUTHORIZED);
    }

    // --- DATABASE HANDLERS (Integrasi ErrorUtil) ---

    // Handle Error Database (Duplicate Entry, Foreign Key, dll)
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ErrorResponse> handleDatabaseException(DataAccessException ex) {
        // Panggil ErrorUtil untuk membersihkan pesan error yang jelek
        ErrorResponse response = errorUtil.errorData(ex);

        // Biasanya error data input user (duplikat) itu 409 Conflict atau 400 Bad
        // Request
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    // --- STANDARD HTTP HANDLERS ---

    // Handle 404 (Path Not Found)
    // Note: Perlu setting di application.yaml:
    // spring.mvc.throw-exception-if-no-handler-found=true
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorResponse> handleNoHandlerFoundException(NoHandlerFoundException ex) {
        return new ResponseEntity<>(
                new ErrorResponse(false, "Path not found: " + ex.getRequestURL()),
                HttpStatus.NOT_FOUND);
    }

    // Handle Socket Timeout
    @ExceptionHandler(SocketTimeoutException.class)
    @ResponseStatus(HttpStatus.REQUEST_TIMEOUT)
    public ResponseEntity<ErrorResponse> handleTimeoutException(SocketTimeoutException ex) {
        return new ResponseEntity<>(
                new ErrorResponse(false, "Request Timeout: " + ex.getMessage()),
                HttpStatus.REQUEST_TIMEOUT);
    }

    // Handle 405 Method Not Allowed
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupported(
            HttpRequestMethodNotSupportedException ex) {
        return new ResponseEntity<>(
                new ErrorResponse(false, "Method not allowed: " + ex.getMethod()),
                HttpStatus.METHOD_NOT_ALLOWED);
    }

    // Handle Missing Parameters
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleMissingParams(MissingServletRequestParameterException ex) {
        return new ResponseEntity<>(
                new ErrorResponse(false, "Required parameter '" + ex.getParameterName() + "' is missing."),
                HttpStatus.BAD_REQUEST);
    }

    // Fix Parameter Type dan Nama Method
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleViolationException(ConstraintViolationException ex) {
        return new ResponseEntity<>(
                new ErrorResponse(false, "Validation Error: " + ex.getMessage()),
                HttpStatus.BAD_REQUEST);
    }

    // Handle Validation Errors (Request Body JSON)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .reduce((msg1, msg2) -> msg1 + ", " + msg2).orElse("Invalid request data");

        return new ResponseEntity<>(
                new ErrorResponse(false, errorMessage),
                HttpStatus.BAD_REQUEST);
    }

    // --- GLOBAL FALLBACK ---

    // Handle 500 Internal Server Error (Sapu Jagat)
    // Pastikan ini ditaruh PALING BAWAH
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex) {
        // Log error asli ke console biar developer tau
        ex.printStackTrace();

        return new ResponseEntity<>(
                new ErrorResponse(false, "Internal Server Error: " + ex.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}