package com.newdawn.tagvote.vote.domain;

import com.newdawn.tagvote.common.domain.BaseTimeEntity;
import com.newdawn.tagvote.question.domain.Question;
import com.newdawn.tagvote.user.domain.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Entity
@Table(name = "vote")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Vote extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "create_user_id", nullable = false)
    private User createdBy;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, length = 20)
    private VoteStatus status;

    @OneToMany(mappedBy = "vote", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Question> questions = new ArrayList<>();

    Vote(final String name, final VoteStatus status) {
        this.name = Objects.requireNonNull(name, "name must not be null");
        this.status = Objects.requireNonNull(status, "status must not be null");
    }

    public void end() {
        this.status = VoteStatus.END;
    }

    public void changeName(final String name) {
        this.name = Objects.requireNonNull(name, "name must not be null");
    }

    public void changeStatus(final VoteStatus status) {
        this.status = Objects.requireNonNull(status, "status must not be null");
    }

    public void assignCreatedBy(final User createdBy) {
        this.createdBy = Objects.requireNonNull(createdBy, "createdBy must not be null");
    }

    public void clearCreatedBy() {
        this.createdBy = null;
    }

    public void addQuestion(final Question question) {
        Objects.requireNonNull(question, "question must not be null");
        questions.add(question);
        question.assignVote(this);
    }

    public void removeQuestion(final Question question) {
        Objects.requireNonNull(question, "question must not be null");
        questions.remove(question);
        question.clearVote();
    }
}
