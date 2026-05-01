package com.newdawn.tagvote.tag.application;

import com.newdawn.tagvote.tag.application.dto.TagCreatedEventResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class TagSseService {

    private final Map<Long, List<SseEmitter>> emittersByVoteId = new ConcurrentHashMap<>();

    public SseEmitter subscribe(final Long voteId) {
        SseEmitter emitter = new SseEmitter(0L);
        emittersByVoteId.computeIfAbsent(voteId, key -> new CopyOnWriteArrayList<>()).add(emitter);

        emitter.onCompletion(() -> removeEmitter(voteId, emitter));
        emitter.onTimeout(() -> removeEmitter(voteId, emitter));
        emitter.onError(ignored -> removeEmitter(voteId, emitter));

        try {
            emitter.send(SseEmitter.event()
                    .name("connected")
                    .data(Map.of("voteId", voteId)));
        } catch (IOException exception) {
            removeEmitter(voteId, emitter);
        }

        return emitter;
    }

    public void publishTagCreated(final TagCreatedEventResponse eventResponse) {
        List<SseEmitter> emitters = emittersByVoteId.getOrDefault(eventResponse.voteId(), List.of());
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event()
                        .name("tag-created")
                        .data(eventResponse));
            } catch (IOException exception) {
                removeEmitter(eventResponse.voteId(), emitter);
            }
        }
    }

    private void removeEmitter(final Long voteId, final SseEmitter emitter) {
        List<SseEmitter> emitters = emittersByVoteId.get(voteId);
        if (emitters == null) {
            return;
        }

        emitters.remove(emitter);
        if (emitters.isEmpty()) {
            emittersByVoteId.remove(voteId);
        }
    }
}
