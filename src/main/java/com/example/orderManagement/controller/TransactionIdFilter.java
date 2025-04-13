package com.example.orderManagement.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

@Component
@Order(1)
@Slf4j
public class TransactionIdFilter extends OncePerRequestFilter {

    public static final String MDC_CORRELATION_ID = "corrId";
    public static final String MDC_REQUEST_ID = "reqId";

    @Override
    public void doFilterInternal(
            HttpServletRequest req,
            HttpServletResponse resp,
            FilterChain chain) throws IOException, ServletException {

        String transactionId = RandomStringUtils.random(6, true, true);
        String correlationId = req.getHeader("X-Correlation-ID");
        if (StringUtils.isBlank(correlationId)) {
            correlationId = UUID.randomUUID().toString();
        }
        MDC.put(MDC_REQUEST_ID, transactionId);
        MDC.put(MDC_CORRELATION_ID, correlationId);

        resp.setHeader("X-Correlation-ID", correlationId);
        resp.setHeader("X-Request-ID", transactionId);


        log.debug(
                "Starting a transaction for req : {} {}",
                transactionId, correlationId);
        try {
            chain.doFilter(req, resp);
        } finally {
            log.debug(
                    "Committing a transaction for req : {} {}",
                    transactionId, correlationId);
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return !request.getServletPath().startsWith(BaseController.BASE_PATH);
    }
}
