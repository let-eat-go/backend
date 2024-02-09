package com.leteatgo.domain.notification.service;

import static org.springframework.http.MediaType.APPLICATION_JSON;

import com.leteatgo.domain.notification.repository.SseEmitterRepository;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
@RequiredArgsConstructor
@Slf4j
public class SseEmitterService {

    private static final long SSE_TIMEOUT = 60 * 60 * 1000L;

    private final SseEmitterRepository sseEmitterRepository;

    public SseEmitter createSseEmitter(String emitterId) {
        return sseEmitterRepository.save(emitterId, new SseEmitter(SSE_TIMEOUT));
    }

    public void deleteSseEmitter(String emitterId) {
        sseEmitterRepository.deleteByEmitterId(emitterId);
    }

    public void send(Object data, String emitterId, SseEmitter sseEmitter) {
        try {
            log.info("send to client {}:[{}]", emitterId, data);
            sseEmitter.send(SseEmitter.event()
                    .id(emitterId)
                    .data(data, APPLICATION_JSON));
        } catch (IOException | IllegalStateException e) {
            log.error("IOException | IllegalStateException is occurred. ", e);
            sseEmitterRepository.deleteByEmitterId(emitterId);
        }
    }
}
