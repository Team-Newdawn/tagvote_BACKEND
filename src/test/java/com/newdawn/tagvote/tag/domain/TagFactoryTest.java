package com.newdawn.tagvote.tag.domain;

import com.newdawn.tagvote.tag.application.dto.TagCreateRequest;
import com.newdawn.tagvote.question.application.dto.QuestionCreateRequest;
import com.newdawn.tagvote.question.domain.Question;
import com.newdawn.tagvote.question.domain.QuestionFactory;
import com.newdawn.tagvote.user.application.dto.UserCreateRequest;
import com.newdawn.tagvote.user.domain.User;
import com.newdawn.tagvote.user.domain.UserFactory;
import com.newdawn.tagvote.vote.application.dto.VoteCreateRequest;
import com.newdawn.tagvote.vote.domain.Vote;
import com.newdawn.tagvote.vote.domain.VoteFactory;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TagFactoryTest {

    @Test
    void createAddsTagToQuestion() {
        User user = UserFactory.create(new UserCreateRequest("creator", "password123"), "encodedPassword");
        Vote vote = VoteFactory.create(new VoteCreateRequest(1L, "vote"), user);
        Question question = QuestionFactory.create(
                new QuestionCreateRequest(1L, "title", "detail", "https://image", 16L),
                vote
        );

        Tag tag = TagFactory.create(
                new TagCreateRequest(1L, TagType.TEXT, "payload", 10, 1.0f, 2.0f),
                question,
                "session-123"
        );

        assertThat(tag.getQuestion()).isEqualTo(question);
        assertThat(question.getTags()).containsExactly(tag);
        assertThat(tag.getSessionId()).isEqualTo("session-123");
    }
}
