package com.example.user_service.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.StringUtils;

public class SessionUtils {
    
    public static String extractIpAddress(HttpServletRequest request) {
        String xForwardedForHeader = request.getHeader("X-Forwarded-For");
        if (StringUtils.hasText(xForwardedForHeader)) {
            return xForwardedForHeader.split(",")[0].trim();
        }
        
        String xRealIpHeader = request.getHeader("X-Real-IP");
        if (StringUtils.hasText(xRealIpHeader)) {
            return xRealIpHeader;
        }
        
        return request.getRemoteAddr();
    }
    
    public static String extractUserAgent(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        return StringUtils.hasText(userAgent) ? userAgent : "Unknown";
    }
    
    public static String generateSessionId(String token) {
        return token.substring(token.length() - 10);
    }
}