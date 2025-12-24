package com.sendistudio.base.domain.services;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.stereotype.Service;

import com.sendistudio.base.app.utils.ErrorUtil;
import com.sendistudio.base.app.utils.FetchDataUtil;
import com.sendistudio.base.data.requests.FetchRequest;
import com.sendistudio.base.data.responses.DataResponse;
import com.sendistudio.base.data.responses.ErrorResponse;
import com.sendistudio.base.data.responses.WebResponse;
import com.sendistudio.base.domain.repositories.SampleRepo;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@Service
public class UserService {
    @Autowired
    private ErrorUtil errorHandler;

    @Autowired
    private SampleRepo repository;

    @Autowired
    private FetchDataUtil fetchData;

    @CircuitBreaker(name = "userService", fallbackMethod = "getAllFallback")
    public WebResponse getAll() {
        try {
            List<String> data = repository.getAll();
            DataResponse<List<String>> response = new DataResponse<>();

            response.setStatus(true);
            response.setData(data);

            return response;
        } catch (CannotGetJdbcConnectionException e) {
            // Database connection error - should throw to trigger circuit breaker
            throw new RuntimeException("Database connection failed", e);
        } catch (EmptyResultDataAccessException e) {
            // Only this should be "not found"
            return errorHandler.errorNotFound(e);
        } catch (DataAccessException e) {
            // Other database errors
            return errorHandler.errorData(e);
        } catch (Exception e) {
            return errorHandler.errorServer(e);
        }
    }

    @CircuitBreaker(name = "userService", fallbackMethod = "getUserContactFallback")
    public WebResponse getUserContact(String token, String logServiceTrx) {
        try {
            String url = "http://127.0.0.1:8080/api/v1/user/gets";

            FetchRequest<Void> request = new FetchRequest<>();
            request.setUrl(url);
            request.setMethod(HttpMethod.GET);

            Map<String, String> headers = Map.of(
                    "X-API-TOKEN", token,
                    "LOG-SERVICE-TRX", logServiceTrx);

            request.setHeaders(headers);

            // Use sendRequestObject to get Map response first
            ResponseEntity<Map<String, Object>> response = fetchData.sendRequestObject(request);

            System.out.println(response.getBody().get("data"));

            if (response.getBody().get("data") == null) {
                System.out.println("masuk null");
                ErrorResponse errorResponse = new ErrorResponse();
                errorResponse.setStatus(false);
                errorResponse.setMessages("User contact not found");
                return errorResponse;

            } else {
                System.out.println("masuk not null");
                DataResponse<Object> dataResponse = new DataResponse<>();

                dataResponse.setStatus(true);
                dataResponse.setData(response.getBody().get("data"));
                return dataResponse;
            }

        } catch (Exception e) {
            // Throw exception to trigger circuit breaker
            throw new RuntimeException("External service connection failed", e);
        }
    }

    // Fallback method for getUserContact when circuit breaker is open
    public WebResponse getUserContactFallback(String token, String logServiceTrx, Exception e) {
        ErrorResponse response = new ErrorResponse();
        response.setStatus(false);
        response.setMessages("User contact service temporarily unavailable. Please try again later.");
        return response;
    }

    // Fallback method when circuit breaker is open
    public WebResponse getAllFallback(Exception e) {
        ErrorResponse response = new ErrorResponse();
        response.setStatus(false);
        response.setMessages("Service temporarily unavailable. Please try again later.");
        return response;
    }
}
