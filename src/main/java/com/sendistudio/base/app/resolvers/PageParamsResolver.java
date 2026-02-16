package com.sendistudio.base.app.resolvers;

import com.sendistudio.base.app.annotations.PageParams;
import com.sendistudio.base.data.models.PagingModel;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class PageParamsResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        // Resolver ini aktif HANYA jika parameter tipe-nya PagingModel DAN ada anotasi @PageParams
        return parameter.getParameterType().equals(PagingModel.class) &&
                parameter.hasParameterAnnotation(PageParams.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {

        // 1. Ambil konfigurasi default dari Annotation
        PageParams pageParams = parameter.getParameterAnnotation(PageParams.class);
        int page = pageParams.defaultPage();
        int size = pageParams.defaultSize();
        int maxSize = pageParams.maxSize();

        // 2. Ambil nilai dari Query Param URL (?page=...&size=...)
        String pageStr = webRequest.getParameter("page");
        String sizeStr = webRequest.getParameter("size");

        // 3. Parsing & Validasi Page
        if (pageStr != null) {
            try {
                int parsedPage = Integer.parseInt(pageStr);
                // Page tidak boleh minus atau 0
                page = parsedPage < 1 ? 1 : parsedPage;
            } catch (NumberFormatException e) {
                // Jika user input ?page=abc, abaikan dan pakai default
            }
        }

        // 4. Parsing & Validasi Size
        if (sizeStr != null) {
            try {
                int parsedSize = Integer.parseInt(sizeStr);
                if (parsedSize < 1) {
                    size = 1; // Minimal 1 data
                } else if (parsedSize > maxSize) {
                    size = maxSize; // Maksimal sesuai setting (safety)
                } else {
                    size = parsedSize;
                }
            } catch (NumberFormatException e) {
                // Abaikan error parsing
            }
        }

        // 5. Return PagingModel totalItems dan totalPages kita set 0 dulu, karena belum query ke DB.
        return new PagingModel(size, 0, page, 0);
    }
}