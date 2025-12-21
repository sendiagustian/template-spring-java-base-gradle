package com.sendistudio.base.data.requests;

import java.util.Map;

import org.springframework.http.HttpMethod;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FetchRequest<T> {
    private String url;
    private HttpMethod method;
    private Map<String, String> headers;
    private T body;
}