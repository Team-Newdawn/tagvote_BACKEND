package com.newdawn.tagvote.user.domain;

import com.newdawn.tagvote.common.domain.BaseTimeEntity;
import com.newdawn.tagvote.vote.domain.Vote;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Getter
@Entity
@Table(name = "user")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String password;

    @Embedded
    private UserRoleSet roleSet;

    @OneToMany(mappedBy = "createdBy", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Vote> votes = new ArrayList<>();

    User(final String name, final String password, final UserRoleSet roleSet) {
        this.name = Objects.requireNonNull(name, "name must not be null");
        this.password = Objects.requireNonNull(password, "password must not be null");
        this.roleSet = Objects.requireNonNull(roleSet, "roleSet must not be null");
    }

    public void changeName(final String name) {
        this.name = Objects.requireNonNull(name, "name must not be null");
    }

    public void changePassword(final String password) {
        this.password = Objects.requireNonNull(password, "password must not be null");
    }

    public void addRole(final UserRoleEnum role) {
        roleSet.addRole(Objects.requireNonNull(role, "role must not be null"));
    }

    public void removeRole(final UserRoleEnum role) {
        roleSet.removeRole(Objects.requireNonNull(role, "role must not be null"));
    }

    public void changeRoles(final Set<UserRoleEnum> roles) {
        roleSet.replaceRoles(new LinkedHashSet<>(Objects.requireNonNull(roles, "roles must not be null")));
    }

    public boolean hasRole(final UserRoleEnum role) {
        return roleSet.hasRole(role);
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roleSet.getAuthorities();
    }

    public void addVote(final Vote vote) {
        Objects.requireNonNull(vote, "vote must not be null");
        votes.add(vote);
        vote.assignCreatedBy(this);
    }

    public void removeVote(final Vote vote) {
        Objects.requireNonNull(vote, "vote must not be null");
        votes.remove(vote);
        vote.clearCreatedBy();
    }
}
