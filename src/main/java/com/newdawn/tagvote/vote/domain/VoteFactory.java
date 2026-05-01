package com.newdawn.tagvote.vote.domain;

import com.newdawn.tagvote.user.domain.User;
import com.newdawn.tagvote.vote.application.dto.VoteCreateRequest;

public final class VoteFactory {

    private VoteFactory() {
    }

    public static Vote create(final VoteCreateRequest request, final User createdBy) {
        Vote vote = new Vote(request.name(), VoteStatus.PROGRESS);
        createdBy.addVote(vote);
        return vote;
    }
}
