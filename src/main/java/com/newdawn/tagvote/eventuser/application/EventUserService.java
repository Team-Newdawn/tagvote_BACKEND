package com.newdawn.tagvote.eventuser.application;

import com.newdawn.tagvote.eventuser.application.dto.EventUserCreateRequest;
import com.newdawn.tagvote.eventuser.application.dto.EventUserResponse;
import com.newdawn.tagvote.eventuser.domain.EventUser;
import com.newdawn.tagvote.eventuser.domain.EventUserFactory;
import com.newdawn.tagvote.eventuser.domain.EventUserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EventUserService {

    private final EventUserRepository eventUserRepository;

    public EventUserService(final EventUserRepository eventUserRepository) {
        this.eventUserRepository = eventUserRepository;
    }

    @Transactional
    public EventUserResponse create(final EventUserCreateRequest request) {
        EventUser eventUser = EventUserFactory.create(request);
        return EventUserResponse.from(eventUserRepository.saveAndFlush(eventUser));
    }
}
