package com.newdawn.tagvote.user.domain;

import com.newdawn.tagvote.user.application.dto.UserCreateRequest;

public final class UserFactory {

    private UserFactory() {
    }

    public static User create(final UserCreateRequest request, final String encodedPassword) {
        return new User(request.name(), encodedPassword, UserRoleSet.getDefault(UserRoleEnum.USER));
    }
}
