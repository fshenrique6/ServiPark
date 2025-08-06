package com.parkinglot.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.util.Date;

/**
 * Filter to log request information.
 */
@WebFilter("/*")
public class LogFilter implements Filter {
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialization of the filter, if needed
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;

        // Captures the start time of the request
        long startTime = System.currentTimeMillis();
        
        try {
            // Processes the request
            chain.doFilter(request, response);
        } finally {
            // Calculates the processing time
            long endTime = System.currentTimeMillis();
            long processingTime = endTime - startTime;

            // Logs request information
            String logMessage = String.format(
                "[%s] %s %s %s (%d ms) - %s",
                new Date(),
                httpRequest.getMethod(),
                httpRequest.getRequestURI(),
                httpRequest.getQueryString() != null ? "?" + httpRequest.getQueryString() : "",
                processingTime,
                httpRequest.getRemoteAddr()
            );

            // In a production environment, use a logging framework like Log4j
            System.out.println("LOG: " + logMessage);
        }
    }
    
    @Override
    public void destroy() {
        // Cleanup code, if needed
    }
} 