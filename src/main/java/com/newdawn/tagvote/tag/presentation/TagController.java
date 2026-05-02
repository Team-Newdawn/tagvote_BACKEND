package com.newdawn.tagvote.tag.presentation;

import com.newdawn.tagvote.tag.application.TagService;
import com.newdawn.tagvote.tag.application.dto.TagCreateRequest;
import com.newdawn.tagvote.tag.application.dto.TagResponse;
import com.newdawn.tagvote.tag.application.dto.TagUpdateRequest;
import com.newdawn.tagvote.global.web.TaglowSessionIdResolver;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TagController {

    private final TagService tagService;
    private final TaglowSessionIdResolver taglowSessionIdResolver;

    public TagController(final TagService tagService, final TaglowSessionIdResolver taglowSessionIdResolver) {
        this.tagService = tagService;
        this.taglowSessionIdResolver = taglowSessionIdResolver;
    }

    @PostMapping("/api/public/questions/{questionId}/tags")
    public ResponseEntity<TagResponse> create(
            @PathVariable final Long questionId,
            @Valid @RequestBody final TagCreateRequest request,
            final HttpServletRequest httpServletRequest
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                tagService.create(questionId, request, taglowSessionIdResolver.require(httpServletRequest))
        );
    }

    @GetMapping("/api/public/questions/{questionId}/tags")
    public ResponseEntity<List<TagResponse>> getByQuestion(
            @PathVariable final Long questionId,
            final HttpServletRequest httpServletRequest
    ) {
        return ResponseEntity.ok(tagService.getTagsByQuestion(questionId, taglowSessionIdResolver.resolve(httpServletRequest)));
    }

    @GetMapping("/api/public/tags/{tagId}")    
    public ResponseEntity<TagResponse> getById(
            @PathVariable final Long tagId,
            final HttpServletRequest httpServletRequest
    ) {
        return ResponseEntity.ok(tagService.getTag(tagId, taglowSessionIdResolver.resolve(httpServletRequest)));
    }

    @PatchMapping("/api/public/tags/{tagId}")
    public ResponseEntity<TagResponse> update(
            @PathVariable final Long tagId,
            @Valid @RequestBody final TagUpdateRequest request,
            final HttpServletRequest httpServletRequest
    ) {
        return ResponseEntity.ok(tagService.update(tagId, request, taglowSessionIdResolver.resolve(httpServletRequest)));
    }

    @DeleteMapping("/api/public/tags/{tagId}")    
    public ResponseEntity<Void> delete(@PathVariable final Long tagId, final HttpServletRequest httpServletRequest) {
        tagService.delete(tagId, taglowSessionIdResolver.require(httpServletRequest));
        return ResponseEntity.noContent().build();
    }
}
