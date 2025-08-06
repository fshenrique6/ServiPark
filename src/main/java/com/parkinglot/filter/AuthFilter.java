package com.parkinglot.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Filter to check user authentication.
 * In this simple version, it only checks if there's a user in the session
 * or if the request is for the login page
 */

 @WebFilter("/admin/*")
public class AuthFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialization of the filter, if needed
    }

     @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false);
        
        String loginURI = httpRequest.getContextPath() + "/login.html";
        boolean isLoginRequest = httpRequest.getRequestURI().equals(loginURI);
        boolean isLoginPage = httpRequest.getRequestURI().endsWith("login.html");
        boolean isResourceRequest = httpRequest.getRequestURI().contains("/resources/");
        boolean isAPIRequest = httpRequest.getRequestURI().contains("/ServiPark");
        
        // allows API access without authentication
        boolean isAllowed = isLoginRequest || isLoginPage || isResourceRequest || isAPIRequest;
        
        // Checks if user is authenticated or if its a allowed request
        boolean isAuthenticated = (session != null && session.getAttribute("user") != null);
        
        if (isAuthenticated || isAllowed) {
            chain.doFilter(request, response);
        } else {
            // Redirects to login page
            httpResponse.sendRedirect(loginURI);
        }
    }

    @Override
    public void destroy() {
        // Cleanup code, if needed
    }
}


