package com.newdawn.tagvote.auth.presentation;

import com.newdawn.tagvote.auth.application.AuthService;
import com.newdawn.tagvote.auth.application.dto.AuthUserResponse;
import com.newdawn.tagvote.auth.application.dto.LoginRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(final AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthUserResponse> login(
            @Valid @RequestBody final LoginRequest request,
            final HttpServletRequest httpServletRequest,
            final HttpServletResponse httpServletResponse
    ) {
        return ResponseEntity.ok(authService.login(request, httpServletRequest, httpServletResponse));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            final HttpServletRequest httpServletRequest,
            final HttpServletResponse httpServletResponse
    ) {
        authService.logout(httpServletRequest, httpServletResponse);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public ResponseEntity<AuthUserResponse> me() {
        return ResponseEntity.ok(authService.me());
    }
}
