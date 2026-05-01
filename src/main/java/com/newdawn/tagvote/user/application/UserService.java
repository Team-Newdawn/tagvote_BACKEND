package com.newdawn.tagvote.user.application;

import com.newdawn.tagvote.global.security.CurrentUserProvider;
import com.newdawn.tagvote.global.security.SessionUserPrincipal;
import com.newdawn.tagvote.user.application.dto.UserCreateRequest;
import com.newdawn.tagvote.user.application.dto.UserResponse;
import com.newdawn.tagvote.user.application.dto.UserRoleUpdateRequest;
import com.newdawn.tagvote.user.application.dto.UserUpdateRequest;
import com.newdawn.tagvote.user.domain.User;
import com.newdawn.tagvote.user.domain.UserFactory;
import com.newdawn.tagvote.user.domain.UserRepository;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CurrentUserProvider currentUserProvider;

    public UserService(
            final UserRepository userRepository,
            final PasswordEncoder passwordEncoder,
            final CurrentUserProvider currentUserProvider
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.currentUserProvider = currentUserProvider;
    }

    @Transactional
    public UserResponse create(final UserCreateRequest request) {
        if (userRepository.existsByName(request.name())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User name is already in use");
        }

        User user = UserFactory.create(request, passwordEncoder.encode(request.password()));
        return UserResponse.from(userRepository.saveAndFlush(user));
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getAll() {
        SessionUserPrincipal principal = currentUserProvider.requireCurrentUser();
        requireAdmin(principal);

        return userRepository.findAll(Sort.by(Sort.Direction.ASC, "id")).stream()
                .map(UserResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public UserResponse getById(final Long userId) {
        SessionUserPrincipal principal = currentUserProvider.requireCurrentUser();
        ensureSelfOrAdmin(principal, userId);
        return UserResponse.from(findUser(userId));
    }

    @Transactional(readOnly = true)
    public UserResponse getCurrentUser() {
        SessionUserPrincipal principal = currentUserProvider.requireCurrentUser();
        return UserResponse.from(findUser(principal.userId()));
    }

    @Transactional
    public UserResponse update(final Long userId, final UserUpdateRequest request) {
        SessionUserPrincipal principal = currentUserProvider.requireCurrentUser();
        ensureSelfOrAdmin(principal, userId);

        User user = findUser(userId);

        if (StringUtils.hasText(request.name()) && !user.getName().equals(request.name())) {
            if (userRepository.existsByName(request.name())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "User name is already in use");
            }
            user.changeName(request.name());
        }

        if (StringUtils.hasText(request.password())) {
            user.changePassword(passwordEncoder.encode(request.password()));
        }

        return UserResponse.from(userRepository.saveAndFlush(user));
    }

    @Transactional
    public UserResponse updateRoles(final Long userId, final UserRoleUpdateRequest request) {
        SessionUserPrincipal principal = currentUserProvider.requireCurrentUser();
        requireAdmin(principal);

        User user = findUser(userId);
        user.changeRoles(request.roles());
        return UserResponse.from(userRepository.saveAndFlush(user));
    }

    @Transactional
    public void delete(final Long userId) {
        SessionUserPrincipal principal = currentUserProvider.requireCurrentUser();
        ensureSelfOrAdmin(principal, userId);
        userRepository.delete(findUser(userId));
    }

    private User findUser(final Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    private void ensureSelfOrAdmin(final SessionUserPrincipal principal, final Long targetUserId) {
        if (!principal.isAdmin() && !principal.userId().equals(targetUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have access to this user");
        }
    }

    private void requireAdmin(final SessionUserPrincipal principal) {
        if (!principal.isAdmin()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Admin role is required");
        }
    }
}
