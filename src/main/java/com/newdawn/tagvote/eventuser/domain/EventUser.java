package com.newdawn.tagvote.eventuser.domain;

import com.newdawn.tagvote.common.domain.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Objects;

@Getter
@Entity
@Table(name = "event_user")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EventUser extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String phone;

    @JdbcTypeCode(SqlTypes.TINYINT)
    @Column(name = "privacy_consent", nullable = false, columnDefinition = "TINYINT")
    private boolean privacyConsent;

    EventUser(final String name, final String phone, final boolean privacyConsent) {
        this.name = Objects.requireNonNull(name, "name must not be null");
        this.phone = Objects.requireNonNull(phone, "phone must not be null");
        this.privacyConsent = privacyConsent;
    }

    public void changeName(final String name) {
        this.name = Objects.requireNonNull(name, "name must not be null");
    }

    public void changePhone(final String phone) {
        this.phone = Objects.requireNonNull(phone, "phone must not be null");
    }

    public void changePrivacyConsent(final boolean privacyConsent) {
        this.privacyConsent = privacyConsent;
    }
}
