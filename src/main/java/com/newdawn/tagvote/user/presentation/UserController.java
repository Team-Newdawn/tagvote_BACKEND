package com.newdawn.tagvote.user.presentation;

import com.newdawn.tagvote.user.application.UserService;
import com.newdawn.tagvote.user.application.dto.UserCreateRequest;
import com.newdawn.tagvote.user.application.dto.UserResponse;
import com.newdawn.tagvote.user.application.dto.UserRoleUpdateRequest;
import com.newdawn.tagvote.user.application.dto.UserUpdateRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(final UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserResponse> create(@Valid @RequestBody final UserCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAll() {
        return ResponseEntity.ok(userService.getAll());
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> me() {
        return ResponseEntity.ok(userService.getCurrentUser());
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getById(@PathVariable final Long userId) {
        return ResponseEntity.ok(userService.getById(userId));
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<UserResponse> update(
            @PathVariable final Long userId,
            @Valid @RequestBody final UserUpdateRequest request
    ) {
        return ResponseEntity.ok(userService.update(userId, request));
    }

    @PatchMapping("/{userId}/roles")
    public ResponseEntity<UserResponse> updateRoles(
            @PathVariable final Long userId,
            @Valid @RequestBody final UserRoleUpdateRequest request
    ) {
        return ResponseEntity.ok(userService.updateRoles(userId, request));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> delete(@PathVariable final Long userId) {
        userService.delete(userId);
        return ResponseEntity.noContent().build();
    }
}
