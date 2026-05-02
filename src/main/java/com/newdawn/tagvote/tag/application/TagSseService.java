package com.newdawn.tagvote.tag.application;

import com.newdawn.tagvote.tag.application.dto.TagCreatedEventResponse;
import com.newdawn.tagvote.tag.application.dto.TagResponse;
import com.newdawn.tagvote.tag.domain.Tag;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class TagSseService {

    private final Map<Long, List<Subscription>> emittersByVoteId = new ConcurrentHashMap<>();

    public SseEmitter subscribe(final Long voteId, final String requestSessionId) {
        SseEmitter emitter = new SseEmitter(0L);
        Subscription subscription = new Subscription(emitter, requestSessionId);
        emittersByVoteId.computeIfAbsent(voteId, key -> new CopyOnWriteArrayList<>()).add(subscription);

        emitter.onCompletion(() -> removeEmitter(voteId, subscription));
        emitter.onTimeout(() -> removeEmitter(voteId, subscription));
        emitter.onError(ignored -> removeEmitter(voteId, subscription));

        try {
            emitter.send(SseEmitter.event()
                    .name("connected")
                    .data(Map.of("voteId", voteId)));
        } catch (IOException exception) {
            removeEmitter(voteId, subscription);
        }

        return emitter;
    }

    public void publishTagCreated(final Long voteId, final Long questionId, final Tag tag) {
        List<Subscription> subscriptions = emittersByVoteId.getOrDefault(voteId, List.of());
        for (Subscription subscription : subscriptions) {
            try {
                subscription.emitter().send(SseEmitter.event()
                        .name("tag-created")
                        .data(new TagCreatedEventResponse(
                                voteId,
                                questionId,
                                TagResponse.from(tag, subscription.requestSessionId())
                        )));
            } catch (IOException exception) {
                removeEmitter(voteId, subscription);
            }
        }
    }

    private void removeEmitter(final Long voteId, final Subscription subscription) {
        List<Subscription> emitters = emittersByVoteId.get(voteId);
        if (emitters == null) {
            return;
        }

        emitters.remove(subscription);
        if (emitters.isEmpty()) {
            emittersByVoteId.remove(voteId);
        }
    }

    private record Subscription(SseEmitter emitter, String requestSessionId) {
    }
}
