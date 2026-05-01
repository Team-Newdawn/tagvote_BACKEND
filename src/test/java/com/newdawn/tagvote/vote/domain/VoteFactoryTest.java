package com.newdawn.tagvote.vote.domain;

import com.newdawn.tagvote.user.application.dto.UserCreateRequest;
import com.newdawn.tagvote.user.domain.User;
import com.newdawn.tagvote.user.domain.UserFactory;
import com.newdawn.tagvote.vote.application.dto.VoteCreateRequest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class VoteFactoryTest {

    @Test
    void createAssignsProgressStatus() {
        User user = UserFactory.create(new UserCreateRequest("creator", "password123"), "encodedPassword");
        VoteCreateRequest request = new VoteCreateRequest(1L, "vote");

        Vote vote = VoteFactory.create(request, user);

        assertThat(vote.getCreatedBy()).isEqualTo(user);
        assertThat(vote.getName()).isEqualTo("vote");
        assertThat(vote.getStatus()).isEqualTo(VoteStatus.PROGRESS);
        assertThat(user.getVotes()).containsExactly(vote);
    }
}
