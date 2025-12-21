package com.sendistudio.base.app.utils;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.sendistudio.base.data.requests.FetchRequest;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class FetchDataUtil {

    private final RestTemplate restTemplate;

    /**
     * Metode utilitas utama untuk melakukan HTTP request generik.
     *
     * @param request      FetchRequest dengan semua parameter request.
     * @param responseType Tipe generik response.
     * @param <T>          Tipe respons.
     * @param <R>          Tipe request body.
     * @return ResponseEntity dengan respons.
     */
    public <R> ResponseEntity<Map<String, Object>> sendRequestObject(FetchRequest<R> request) {
        // Inisialisasi header kosong jika null
        HttpHeaders httpHeaders = new HttpHeaders();
        if (request.getHeaders() != null) {
            request.getHeaders().forEach(httpHeaders::set);
        }

        // Siapkan HttpEntity dengan body (bisa null)
        HttpEntity<R> entity = new HttpEntity<>(request.getBody(), httpHeaders);

        // Mengirim request menggunakan RestTemplate
        return restTemplate.exchange(Objects.requireNonNull(request.getUrl()),
                Objects.requireNonNull(request.getMethod()), entity,
                new ParameterizedTypeReference<Map<String, Object>>() {
                });
    }

    public <R> ResponseEntity<List<Object>> sendRequestList(FetchRequest<R> request) {
        // Inisialisasi header kosong jika null
        HttpHeaders httpHeaders = new HttpHeaders();
        if (request.getHeaders() != null) {
            request.getHeaders().forEach(httpHeaders::set);
        }

        // Siapkan HttpEntity dengan body (bisa null)
        HttpEntity<R> entity = new HttpEntity<>(request.getBody(), httpHeaders);

        // Mengirim request menggunakan RestTemplate
        return restTemplate.exchange(Objects.requireNonNull(request.getUrl()),
                Objects.requireNonNull(request.getMethod()), entity,
                new ParameterizedTypeReference<List<Object>>() {
                });
    }
}
