package com.example.spring_boot_rate_limiting;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class RateLimitingFilter implements Filter {
    private static final int MAX_REQUEST = 5;
    private static final long WINDOW_MS = 60000;
    private final ConcurrentHashMap<String, RequestWindow> requestCounts = new ConcurrentHashMap<>();
    private static class RequestWindow
    {
        int count;
        long startTime;
        RequestWindow(long startTime)
        {
            this.count = 1;
            this.startTime = startTime;
        }
    }
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;
        String clientIp = httpRequest.getRemoteAddr();
        long currentTime = System.currentTimeMillis();

        //Reset count after 60 seconds
        RequestWindow window = requestCounts.compute(clientIp,(ip,existing)->{
            if(existing == null || currentTime-existing.startTime>=WINDOW_MS)
            {
                    return new RequestWindow(currentTime);
            }
            existing.count++;
            return existing;
        });
        if(window.count > MAX_REQUEST)
        {
            httpResponse.setStatus(429);
            httpResponse.getWriter().write("Too Many Request");
            return;
        }

        filterChain.doFilter(servletRequest,servletResponse);
    }
}
