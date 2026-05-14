package com.newdawn.tagvote.vote.application.dto;

import java.util.List;

public record VoteResponseWithCount(
        int voteCount,
        List<VoteResponse> voteResponse
) {
}
