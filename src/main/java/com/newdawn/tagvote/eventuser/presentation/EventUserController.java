package com.newdawn.tagvote.eventuser.presentation;

import com.newdawn.tagvote.eventuser.application.EventUserService;
import com.newdawn.tagvote.eventuser.application.dto.EventUserCreateRequest;
import com.newdawn.tagvote.eventuser.application.dto.EventUserResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public/event-users")
public class EventUserController {

    private final EventUserService eventUserService;

    public EventUserController(final EventUserService eventUserService) {
        this.eventUserService = eventUserService;
    }

    @PostMapping
    public ResponseEntity<EventUserResponse> create(@Valid @RequestBody final EventUserCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(eventUserService.create(request));
    }
}
