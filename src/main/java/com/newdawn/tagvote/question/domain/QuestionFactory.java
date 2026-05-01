package com.newdawn.tagvote.question.domain;

import com.newdawn.tagvote.question.application.dto.QuestionCreateRequest;
import com.newdawn.tagvote.vote.domain.Vote;

public final class QuestionFactory {

    private QuestionFactory() {
    }

    public static Question create(final QuestionCreateRequest request, final Vote vote) {
        Question question = new Question(
                request.title(),
                request.detail(),
                request.imageUrl(),
                request.imageRatio()
        );
        vote.addQuestion(question);
        return question;
    }
}
