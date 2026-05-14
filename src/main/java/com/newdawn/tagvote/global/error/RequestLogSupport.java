package com.newdawn.tagvote.global.error;

import jakarta.servlet.http.HttpServletRequest;

public final class RequestLogSupport {

    private RequestLogSupport() {
    }

    public static String requestPath(final HttpServletRequest request) {
        String queryString = request.getQueryString();
        if (queryString == null || queryString.isBlank()) {
            return request.getRequestURI();
        }

        return request.getRequestURI() + "?" + queryString;
    }

    public static String errorLogMessage(
            final HttpServletRequest request,
            final int statusCode,
            final String message
    ) {
        return "[ERROR] " + request.getMethod()
                + " " + requestPath(request)
                + " - status=" + statusCode
                + " message=" + fallbackMessage(message, "Unexpected error");
    }

    public static String fallbackMessage(final String message, final String fallback) {
        if (message == null || message.isBlank()) {
            return fallback;
        }

        return message;
    }
}
