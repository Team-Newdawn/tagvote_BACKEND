package com.newdawn.tagvote.global.security;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class CurrentUserProvider {

    public SessionUserPrincipal getCurrentUserOrNull() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return null;
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof SessionUserPrincipal sessionUserPrincipal) {
            return sessionUserPrincipal;
        }
        return null;
    }

    public SessionUserPrincipal requireCurrentUser() {
        SessionUserPrincipal principal = getCurrentUserOrNull();
        if (principal == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication is required");
        }
        return principal;
    }
}
