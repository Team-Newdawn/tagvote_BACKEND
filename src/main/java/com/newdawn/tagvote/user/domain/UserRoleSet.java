package com.newdawn.tagvote.user.domain;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserRoleSet {

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private Set<UserRoleEnum> roles = new HashSet<>();

    public UserRoleSet(final Collection<UserRoleEnum> roles) {
        this.roles.addAll(roles);
    }

    public void addRole(final UserRoleEnum userRoleEnum) {
        roles.add(userRoleEnum);
    }

    public void removeRole(final UserRoleEnum userRoleEnum) {
        roles.remove(userRoleEnum);
    }

    public void replaceRoles(final Collection<UserRoleEnum> newRoles) {
        roles.clear();
        roles.addAll(newRoles);
    }

    public boolean hasRole(final UserRoleEnum role) {
        return roles.contains(role);
    }

    public static UserRoleSet getDefault(final UserRoleEnum role) {
        Set<UserRoleEnum> defaultRoles = new HashSet<>();
        defaultRoles.add(role);
        return new UserRoleSet(defaultRoles);
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getAuthority()))
                .collect(Collectors.toSet());
    }

    public Set<UserRoleEnum> getRoles() {
        return Set.copyOf(roles);
    }
}
