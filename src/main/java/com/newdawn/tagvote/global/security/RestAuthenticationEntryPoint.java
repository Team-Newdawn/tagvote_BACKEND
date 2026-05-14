package com.newdawn.tagvote.global.security;

import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.newdawn.tagvote.global.error.ApiErrorResponse;
import com.newdawn.tagvote.global.error.RequestLogSupport;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static final Logger log = LoggerFactory.getLogger(RestAuthenticationEntryPoint.class);
    private static final ObjectWriter objectWriter = JsonMapper.builder()
            .findAndAddModules()
            .build()
            .writer();

    @Override
    public void commence(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final AuthenticationException authenticationException
    ) throws IOException {
        String message = "Authentication is required";
        log.error(RequestLogSupport.errorLogMessage(request, HttpStatus.FORBIDDEN.value(), message));

        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectWriter.writeValue(response.getWriter(), ApiErrorResponse.of(HttpStatus.FORBIDDEN, message, request));
    }
}
