package com.sendistudio.base.app.resolvers;

import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.sendistudio.base.app.annotations.SortParams;
import com.sendistudio.base.data.models.SortingModel;

import java.util.Arrays;
import java.util.List;

@Component
public class SortParamsResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        // Resolver aktif jika parameter tipe SortingModel dan ada anotasi @SortParams
        return parameter.getParameterType().equals(SortingModel.class) &&
                parameter.hasParameterAnnotation(SortParams.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {

        // 1. Ambil konfigurasi dari annotation
        SortParams sortParams = parameter.getParameterAnnotation(SortParams.class);
        String defaultSortBy = "";
        String defaultDirection = "ASC";
        String[] allowedFields = new String[0];
        
        if (sortParams != null) {
            defaultSortBy = sortParams.defaultSortBy();
            defaultDirection = sortParams.defaultDirection();
            allowedFields = sortParams.allowedFields();
        }

        // 2. Ambil nilai dari Query Param URL (?sortBy=...&sortDirection=...)
        String sortBy = webRequest.getParameter("sortBy");
        String sortDirection = webRequest.getParameter("sortDirection");

        // 3. Jika tidak ada di query param, gunakan default
        if (sortBy == null || sortBy.trim().isEmpty()) {
            sortBy = defaultSortBy;
        }
        
        if (sortDirection == null || sortDirection.trim().isEmpty()) {
            sortDirection = defaultDirection;
        }

        // 4. Validasi sortBy terhadap allowedFields (security check)
        if (allowedFields.length > 0 && sortBy != null && !sortBy.trim().isEmpty()) {
            List<String> allowed = Arrays.asList(allowedFields);
            if (!allowed.contains(sortBy)) {
                // Jika field tidak diizinkan, fallback ke default atau kosongkan
                sortBy = defaultSortBy;
            }
        }

        // 5. Validasi sortDirection (hanya ASC atau DESC)
        if (sortDirection != null) {
            String upper = sortDirection.trim().toUpperCase();
            if (!upper.equals("ASC") && !upper.equals("DESC")) {
                sortDirection = defaultDirection;
            } else {
                sortDirection = upper;
            }
        }

        // 6. Return SortingModel
        return new SortingModel(sortBy, sortDirection);
    }
}
