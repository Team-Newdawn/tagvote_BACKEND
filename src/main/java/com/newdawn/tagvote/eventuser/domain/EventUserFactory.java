package com.newdawn.tagvote.eventuser.domain;

import com.newdawn.tagvote.eventuser.application.dto.EventUserCreateRequest;

public final class EventUserFactory {

    private EventUserFactory() {
    }

    public static EventUser create(final EventUserCreateRequest request) {
        return new EventUser(request.name(), request.phone(), request.privacyConsent());
    }
}
