package com.sendistudio.base.app.helpers;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class IpHelper {

    private static final String[] IP_HEADER_CANDIDATES = {
        "X-Forwarded-For",
        "Proxy-Client-IP",
        "WL-Proxy-Client-IP",
        "HTTP_X_FORWARDED_FOR",
        "HTTP_X_FORWARDED",
        "HTTP_X_CLUSTER_CLIENT_IP",
        "HTTP_CLIENT_IP",
        "HTTP_FORWARDED_FOR",
        "HTTP_FORWARDED",
        "X-Real-IP", 
        "REMOTE_ADDR"
    };

    public String getClientIp(HttpServletRequest request) {
        for (String header : IP_HEADER_CANDIDATES) {
            String ip = request.getHeader(header);
            
            if (isValidIp(ip)) {
                // X-Forwarded-For seringkali berisi daftar IP: "client, proxy1, proxy2"
                // Kita ambil yang paling KIRI (Client asli)
                return ip.split(",")[0].trim();
            }
        }
        
        // Fallback terakhir: Ambil dari koneksi langsung
        return request.getRemoteAddr();
    }

    private boolean isValidIp(String ip) {
        return StringUtils.hasText(ip) && !"unknown".equalsIgnoreCase(ip);
    }
}