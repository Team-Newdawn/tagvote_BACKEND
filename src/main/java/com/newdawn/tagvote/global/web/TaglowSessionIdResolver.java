package com.newdawn.tagvote.global.web;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

@Component
public class TaglowSessionIdResolver {

    public static final String HEADER_NAME = "X-Taglow-Session-Id";
    private static final String LEGACY_HEADER_NAME = "taglow-Session-Id";

    public String resolve(final HttpServletRequest request) {
        String primaryHeaderValue = normalize(request.getHeader(HEADER_NAME));
        if (primaryHeaderValue != null) {
            return primaryHeaderValue;
        }
        return normalize(request.getHeader(LEGACY_HEADER_NAME));
    }

    public String require(final HttpServletRequest request) {
        String sessionId = resolve(request);
        if (sessionId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, HEADER_NAME + " header is required");
        }
        return sessionId;
    }

    private String normalize(final String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return value.trim();
    }
}
