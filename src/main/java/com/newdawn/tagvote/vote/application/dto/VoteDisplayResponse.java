package com.newdawn.tagvote.vote.application.dto;

import com.newdawn.tagvote.question.application.dto.QuestionWithTagsResponse;
import com.newdawn.tagvote.vote.domain.VoteStatus;

import java.util.List;

public record VoteDisplayResponse(
        Long voteId,
        String voteName,
        VoteStatus status,
        List<QuestionWithTagsResponse> questions
) {
}
