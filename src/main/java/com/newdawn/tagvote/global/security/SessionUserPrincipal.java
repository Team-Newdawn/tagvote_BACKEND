package com.newdawn.tagvote.global.security;

import com.newdawn.tagvote.user.domain.User;
import com.newdawn.tagvote.user.domain.UserRoleEnum;
import org.springframework.security.core.GrantedAuthority;

import java.io.Serializable;
import java.util.Collection;
import java.util.Set;

public record SessionUserPrincipal(
        Long userId,
        String name,
        Set<UserRoleEnum> roles,
        Collection<? extends GrantedAuthority> authorities
) implements Serializable {

    public static SessionUserPrincipal from(final User user) {
        return new SessionUserPrincipal(
                user.getId(),
                user.getName(),
                user.getRoleSet().getRoles(),
                user.getAuthorities()
        );
    }

    public boolean isAdmin() {
        return roles.contains(UserRoleEnum.ADMIN);
    }
}
