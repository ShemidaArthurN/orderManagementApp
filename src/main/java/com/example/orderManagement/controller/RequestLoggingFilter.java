package com.example.orderManagement.controller;

import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Component
public class RequestLoggingFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialization logic (optional)
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        if (request instanceof HttpServletRequest) {
            HttpServletRequest httpRequest = (HttpServletRequest) request;

            // Log details of the HTTP request
            System.out.println("HTTP Method: " + httpRequest.getMethod());
            System.out.println("Request URI: " + httpRequest.getRequestURI());
            System.out.println("Query Parameters: " + httpRequest.getQueryString());
            System.out.println("User-Agent: " + httpRequest.getHeader("User-Agent"));
        }

        // Continue with the filter chain
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // Cleanup logic (optional)
    }
}