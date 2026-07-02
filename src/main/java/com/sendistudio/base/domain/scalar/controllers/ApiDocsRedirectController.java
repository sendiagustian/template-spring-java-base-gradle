package com.sendistudio.base.domain.scalar.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sendistudio.base.constants.ScalarTagConst;
import com.sendistudio.base.domain.scalar.services.FaviconService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

/**
 * Controller to handle trailing slash redirects for API documentation
 * endpoints.
 * Fixes issue where Scalar UI tries to fetch /v3/api-docs/ but SpringDoc only
 * serves /v3/api-docs
 */
@RestController
@RequiredArgsConstructor
@Tag(name = ScalarTagConst.API_DOCS)
public class ApiDocsRedirectController {

    private final FaviconService faviconService;

    @GetMapping("/v3/api-docs/")
    public ResponseEntity<Void> redirectV3ApiDocs(HttpServletResponse response) {
        response.setHeader("Location", "/v3/api-docs");
        return ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY).build();
    }

    @GetMapping("/favicon.svg")
    public ResponseEntity<String> favicon() {
        String svg = faviconService.loadFaviconSvg();
        return ResponseEntity.ok()
                .header("Content-Type", "image/svg+xml")
                .header("Cache-Control", "public, max-age=86400")
                .body(svg);
    }

    @GetMapping("/favicon.ico")
    public ResponseEntity<Void> faviconIco(HttpServletResponse response) {
        response.setHeader("Location", "/favicon.svg");
        return ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY).build();
    }
}