package com.newdawn.tagvote.auth.application;

import com.newdawn.tagvote.auth.application.dto.AuthUserResponse;
import com.newdawn.tagvote.auth.application.dto.LoginRequest;
import com.newdawn.tagvote.global.security.CurrentUserProvider;
import com.newdawn.tagvote.global.security.SessionUserPrincipal;
import com.newdawn.tagvote.user.domain.User;
import com.newdawn.tagvote.user.domain.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SecurityContextRepository securityContextRepository;
    private final CurrentUserProvider currentUserProvider;

    public AuthService(
            final UserRepository userRepository,
            final PasswordEncoder passwordEncoder,
            final SecurityContextRepository securityContextRepository,
            final CurrentUserProvider currentUserProvider
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.securityContextRepository = securityContextRepository;
        this.currentUserProvider = currentUserProvider;
    }

    @Transactional(readOnly = true)
    public AuthUserResponse login(
            final LoginRequest request,
            final HttpServletRequest httpServletRequest,
            final HttpServletResponse httpServletResponse
    ) {
        User user = userRepository.findByName(request.name())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid login credentials"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid login credentials");
        }

        SessionUserPrincipal principal = SessionUserPrincipal.from(user);
        UsernamePasswordAuthenticationToken authentication =
                UsernamePasswordAuthenticationToken.authenticated(principal, null, principal.authorities());

        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);
        securityContextRepository.saveContext(securityContext, httpServletRequest, httpServletResponse);

        return AuthUserResponse.from(principal);
    }

    public void logout(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse) {
        HttpSession session = httpServletRequest.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        SecurityContextHolder.clearContext();
        securityContextRepository.saveContext(
                SecurityContextHolder.createEmptyContext(),
                httpServletRequest,
                httpServletResponse
        );
    }

    public AuthUserResponse me() {
        return AuthUserResponse.from(currentUserProvider.requireCurrentUser());
    }
}
