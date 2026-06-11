package com.sendistudio.base.domain.scalar.services;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Service
public class FaviconService {

    public String loadFaviconSvg() {
        try {
            ClassPathResource res = new ClassPathResource("assets/favicon.svg");
            if (!res.exists()) {
                throw new java.io.FileNotFoundException("assets/favicon.svg not found on classpath");
            }

            try (InputStream is = res.getInputStream()) {
                return new String(is.readAllBytes(), StandardCharsets.UTF_8);
            }
        } catch (java.io.FileNotFoundException e) {
            throw new RuntimeException("Favicon not found: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load favicon: " + e.getMessage(), e);
        }
    }
}
