package com.newdawn.tagvote.question.domain;

import com.newdawn.tagvote.common.domain.BaseTimeEntity;
import com.newdawn.tagvote.tag.domain.Tag;
import com.newdawn.tagvote.vote.domain.Vote;
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
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Entity
@Table(name = "question")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Question extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vote_id", nullable = false)
    private Vote vote;

    @Column
    private String title;

    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Column(columnDefinition = "TEXT")
    private String detail;

    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Column(name = "image_url", nullable = false, columnDefinition = "TEXT")
    private String imageUrl;

    @Column(name = "image_ratio", nullable = false)
    private Long imageRatio;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Tag> tags = new ArrayList<>();

    Question(final String title, final String detail, final String imageUrl, final Long imageRatio) {
        this.title = title;
        this.detail = detail;
        this.imageUrl = Objects.requireNonNull(imageUrl, "imageUrl must not be null");
        this.imageRatio = Objects.requireNonNull(imageRatio, "imageRatio must not be null");
    }

    public void changeTitle(final String title) {
        this.title = title;
    }

    public void changeDetail(final String detail) {
        this.detail = detail;
    }

    public void changeImageUrl(final String imageUrl) {
        this.imageUrl = Objects.requireNonNull(imageUrl, "imageUrl must not be null");
    }

    public void changeImageRatio(final Long imageRatio) {
        this.imageRatio = Objects.requireNonNull(imageRatio, "imageRatio must not be null");
    }

    public void addTag(final Tag tag) {
        Objects.requireNonNull(tag, "tag must not be null");
        tags.add(tag);
        tag.assignQuestion(this);
    }

    public void removeTag(final Tag tag) {
        Objects.requireNonNull(tag, "tag must not be null");
        tags.remove(tag);
        tag.clearQuestion();
    }

    public void assignVote(final Vote vote) {
        this.vote = Objects.requireNonNull(vote, "vote must not be null");
    }

    public void clearVote() {
        this.vote = null;
    }
}
