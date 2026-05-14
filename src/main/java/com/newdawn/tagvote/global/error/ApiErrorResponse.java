package com.newdawn.tagvote.global.error;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import java.time.Instant;

public record ApiErrorResponse(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path
) {

    public static ApiErrorResponse of(
            final HttpStatusCode statusCode,
            final String message,
            final HttpServletRequest request
    ) {
        HttpStatus httpStatus = HttpStatus.resolve(statusCode.value());
        String error = httpStatus != null ? httpStatus.getReasonPhrase() : "Unknown Error";

        return new ApiErrorResponse(
                Instant.now(),
                statusCode.value(),
                error,
                message,
                RequestLogSupport.requestPath(request)
        );
    }
}
