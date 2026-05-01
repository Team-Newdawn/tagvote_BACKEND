package com.newdawn.tagvote.tag.domain;

import com.newdawn.tagvote.common.domain.BaseTimeEntity;
import com.newdawn.tagvote.question.domain.Question;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Objects;

@Getter
@Entity
@Table(name = "tag")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Tag extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @Column(length = 20)
    private TagType type;

    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Column(nullable = false, columnDefinition = "TEXT")
    private String data;

    @Column(nullable = false)
    private Integer duration;

    @Column(name = "location_x")
    private Float locationX;

    @Column(name = "location_y")
    private Float locationY;

    Tag(final TagType type, final String data, final Integer duration, final Float locationX, final Float locationY) {
        this.type = type;
        this.data = Objects.requireNonNull(data, "data must not be null");
        this.duration = Objects.requireNonNull(duration, "duration must not be null");
        this.locationX = locationX;
        this.locationY = locationY;
    }

    public void changeType(final TagType type) {
        this.type = type;
    }

    public void changeData(final String data) {
        this.data = Objects.requireNonNull(data, "data must not be null");
    }

    public void changeDuration(final Integer duration) {
        this.duration = Objects.requireNonNull(duration, "duration must not be null");
    }

    public void changeLocationX(final Float locationX) {
        this.locationX = locationX;
    }

    public void changeLocationY(final Float locationY) {
        this.locationY = locationY;
    }

    public void assignQuestion(final Question question) {
        this.question = Objects.requireNonNull(question, "question must not be null");
    }

    public void clearQuestion() {
        this.question = null;
    }
}
