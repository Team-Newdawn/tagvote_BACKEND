package com.newdawn.tagvote.vote.presentation;

import com.newdawn.tagvote.tag.application.TagSseService;
import com.newdawn.tagvote.vote.application.VoteService;
import com.newdawn.tagvote.vote.application.dto.VoteCreateRequest;
import com.newdawn.tagvote.vote.application.dto.VoteDisplayResponse;
import com.newdawn.tagvote.vote.application.dto.VoteResponse;
import com.newdawn.tagvote.vote.application.dto.VoteUpdateRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
public class VoteController {

    private final VoteService voteService;
    private final TagSseService tagSseService;

    public VoteController(final VoteService voteService, final TagSseService tagSseService) {
        this.voteService = voteService;
        this.tagSseService = tagSseService;
    }

    @PostMapping("/api/votes")
    public ResponseEntity<VoteResponse> create(@Valid @RequestBody final VoteCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(voteService.create(request));
    }

    @GetMapping("/api/votes")
    public ResponseEntity<List<VoteResponse>> getAll() {
        return ResponseEntity.ok(voteService.getAllAccessibleVotes());
    }

    @GetMapping("/api/votes/{voteId}")
    public ResponseEntity<VoteResponse> getById(@PathVariable final Long voteId) {
        return ResponseEntity.ok(voteService.getAccessibleVote(voteId));
    }

    @PatchMapping("/api/votes/{voteId}")
    public ResponseEntity<VoteResponse> update(
            @PathVariable final Long voteId,
            @Valid @RequestBody final VoteUpdateRequest request
    ) {
        return ResponseEntity.ok(voteService.update(voteId, request));
    }

    @DeleteMapping("/api/votes/{voteId}")
    public ResponseEntity<Void> delete(@PathVariable final Long voteId) {
        voteService.delete(voteId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/api/public/votes/{voteId}/display")
    public ResponseEntity<VoteDisplayResponse> getDisplay(@PathVariable final Long voteId) {
        return ResponseEntity.ok(voteService.getPublicVoteDisplay(voteId));
    }

    @GetMapping("/api/public/votes/{voteId}/events")
    public SseEmitter subscribe(@PathVariable final Long voteId) {
        return tagSseService.subscribe(voteId);
    }
}
