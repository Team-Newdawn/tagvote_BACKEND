package com.newdawn.tagvote.question.application.dto;

import com.newdawn.tagvote.tag.application.dto.TagResponse;

import java.util.List;

public record QuestionWithTagsResponse(
        QuestionResponse question,
        List<TagResponse> tags
) {
}
