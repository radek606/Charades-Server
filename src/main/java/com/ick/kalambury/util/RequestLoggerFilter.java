package com.ick.kalambury.util;

import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Order(1)
public class RequestLoggerFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException
    {
        try {
            filterChain.doFilter(request, response);
        }
        finally {
            if (!isAsyncStarted(request)) {
                logger.info(createMessage(request, response));
            }
        }
    }

    protected String createMessage(HttpServletRequest request, HttpServletResponse response) {
        StringBuilder msg = new StringBuilder();

        msg.append(request.getRemoteAddr()).append(" - ");
        msg.append(request.getMethod()).append(" ");
        msg.append(request.getRequestURI());

        String queryString = request.getQueryString();
        if (queryString != null) {
            msg.append('?').append(queryString).append(" ");
        } else {
            msg.append(" ");
        }

        msg.append(request.getProtocol()).append(" ");
        msg.append("\"").append(request.getHeader("User-Agent")).append("\" ");
        msg.append(response.getStatus());

        return msg.toString();
    }

}
