package com.leteatgo.domain.notification.repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Repository
public class SseEmitterRepository {

    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    public SseEmitter save(String emitterId, SseEmitter emitter) {
        emitters.put(emitterId, emitter);
        return emitter;
    }

    public void deleteByEmitterId(String emitterId) {
        emitters.remove(emitterId);
    }

    public SseEmitter findByEmitterId(String emitterId) {
        return emitters.get(emitterId);
    }
}
