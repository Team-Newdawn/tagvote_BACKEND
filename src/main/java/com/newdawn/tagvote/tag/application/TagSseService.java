package com.newdawn.tagvote.tag.application;

import com.newdawn.tagvote.tag.application.dto.TagCreatedEventResponse;
import com.newdawn.tagvote.tag.application.dto.TagResponse;
import com.newdawn.tagvote.tag.domain.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class TagSseService {

    private static final Logger log = LoggerFactory.getLogger(TagSseService.class);

    private final Map<Long, List<Subscription>> emittersByVoteId = new ConcurrentHashMap<>();
    private final long timeoutMs;
    private final int highSubscriberThreshold;
    private final int maxSubscriberCount;

    public TagSseService(
            @Value("${tagvote.sse.timeout-ms:300000}") final long timeoutMs,
            @Value("${tagvote.sse.high-subscriber-threshold:200}") final int highSubscriberThreshold,
            @Value("${tagvote.sse.max-subscriber-count:1000}") final int maxSubscriberCount
    ) {
        this.timeoutMs = timeoutMs;
        this.highSubscriberThreshold = highSubscriberThreshold;
        this.maxSubscriberCount = maxSubscriberCount;
    }

    public SseEmitter subscribe(final Long voteId, final String requestSessionId) {
        if (totalSubscriberCount() >= maxSubscriberCount) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Too many live event connections");
        }

        SseEmitter emitter = new SseEmitter(timeoutMs);
        Subscription subscription = new Subscription(emitter, requestSessionId);
        List<Subscription> subscriptions = emittersByVoteId.computeIfAbsent(voteId, key -> new CopyOnWriteArrayList<>());
        replaceExistingSubscription(subscriptions, voteId, requestSessionId);
        subscriptions.add(subscription);

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

        logIfSubscriberCountHigh(voteId);
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

    @Scheduled(fixedDelayString = "${tagvote.sse.heartbeat-interval-ms:30000}")
    public void sendHeartbeat() {
        for (Map.Entry<Long, List<Subscription>> entry : emittersByVoteId.entrySet()) {
            Long voteId = entry.getKey();
            for (Subscription subscription : entry.getValue()) {
                try {
                    subscription.emitter().send(SseEmitter.event()
                            .name("heartbeat")
                            .data(Map.of(
                                    "voteId", voteId,
                                    "timestamp", Instant.now().toString()
                            )));
                } catch (IOException exception) {
                    removeEmitter(voteId, subscription);
                }
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

    private void replaceExistingSubscription(
            final List<Subscription> subscriptions,
            final Long voteId,
            final String requestSessionId
    ) {
        if (!StringUtils.hasText(requestSessionId)) {
            return;
        }

        for (Subscription existing : subscriptions) {
            if (requestSessionId.equals(existing.requestSessionId())) {
                subscriptions.remove(existing);
                try {
                    existing.emitter().complete();
                } catch (Exception ignored) {
                    removeEmitter(voteId, existing);
                }
            }
        }
    }

    private void logIfSubscriberCountHigh(final Long voteId) {
        int voteSubscriberCount = emittersByVoteId.getOrDefault(voteId, List.of()).size();
        int totalSubscriberCount = totalSubscriberCount();

        if (voteSubscriberCount >= highSubscriberThreshold || totalSubscriberCount >= highSubscriberThreshold) {
            log.error(
                    "[ERROR] SSE subscriber count is high - voteId={} voteSubscribers={} totalSubscribers={}",
                    voteId,
                    voteSubscriberCount,
                    totalSubscriberCount
            );
        }
    }

    private int totalSubscriberCount() {
        return emittersByVoteId.values().stream()
                .mapToInt(List::size)
                .sum();
    }

    private record Subscription(SseEmitter emitter, String requestSessionId) {
    }
}
