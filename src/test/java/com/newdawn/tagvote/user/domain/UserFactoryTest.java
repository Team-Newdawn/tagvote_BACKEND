package com.newdawn.tagvote.user.domain;

import com.newdawn.tagvote.user.application.dto.UserCreateRequest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserFactoryTest {

    @Test
    void createAssignsDefaultUserRole() {
        User user = UserFactory.create(new UserCreateRequest("tester", "password123"), "encodedPassword");

        assertThat(user.getName()).isEqualTo("tester");
        assertThat(user.getPassword()).isEqualTo("encodedPassword");
        assertThat(user.hasRole(UserRoleEnum.USER)).isTrue();
        assertThat(user.hasRole(UserRoleEnum.ADMIN)).isFalse();
        assertThat(user.getAuthorities())
                .extracting("authority")
                .containsExactly(UserRoleEnum.Authority.USER);
    }
}
