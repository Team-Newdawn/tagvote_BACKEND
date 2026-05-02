package com.newdawn.tagvote.tag.domain;

import com.newdawn.tagvote.tag.application.dto.TagCreateRequest;
import com.newdawn.tagvote.question.domain.Question;

public final class TagFactory {

    private TagFactory() {
    }

    public static Tag create(final TagCreateRequest request, final Question question, final String sessionId) {
        Tag tag = new Tag(
                request.type(),
                request.data(),
                request.duration(),
                request.locationX(),
                request.locationY(),
                sessionId
        );
        question.addTag(tag);
        return tag;
    }
}
