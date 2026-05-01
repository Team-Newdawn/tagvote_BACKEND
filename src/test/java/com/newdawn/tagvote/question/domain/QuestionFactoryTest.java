package com.newdawn.tagvote.question.domain;

import com.newdawn.tagvote.question.application.dto.QuestionCreateRequest;
import com.newdawn.tagvote.user.application.dto.UserCreateRequest;
import com.newdawn.tagvote.user.domain.User;
import com.newdawn.tagvote.user.domain.UserFactory;
import com.newdawn.tagvote.vote.application.dto.VoteCreateRequest;
import com.newdawn.tagvote.vote.domain.Vote;
import com.newdawn.tagvote.vote.domain.VoteFactory;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class QuestionFactoryTest {

    @Test
    void createAddsQuestionToVote() {
        User user = UserFactory.create(new UserCreateRequest("creator", "password123"), "encodedPassword");
        Vote vote = VoteFactory.create(new VoteCreateRequest(1L, "vote"), user);

        Question question = QuestionFactory.create(
                new QuestionCreateRequest(1L, "title", "detail", "https://image", 16L),
                vote
        );

        assertThat(question.getVote()).isEqualTo(vote);
        assertThat(question.getImageUrl()).isEqualTo("https://image");
        assertThat(question.getImageRatio()).isEqualTo(16L);
        assertThat(vote.getQuestions()).containsExactly(question);
    }
}
