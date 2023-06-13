package com.hhkbdev.springsse.sse;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
public class SseService {

  private final Logger log = org.slf4j.LoggerFactory.getLogger(SseService.class);

  private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

  public SseEmitter getSseEmitterByUserId(String userId) {
    if (emitters.containsKey(userId)) {
      return emitters.get(userId);
    }

    return getNewSseEmitter(userId);
  }

  public void sendEventData(String userId, Object data) {
    try {
      SseEmitter sseEmitter = emitters.get(userId);
      if (sseEmitter != null) {
        sseEmitter.send(data);
      }

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private SseEmitter getNewSseEmitter(String userId) {
    SseEmitter sseEmitter = new SseEmitter(Long.MAX_VALUE);
    sseEmitter.onCompletion(() -> {
      log.info("SSE connection closed");
      removeEmitter(userId);
    });

    sseEmitter.onTimeout(() -> {
      log.info("SSE connection timed out");
      removeEmitter(userId);
    });

    sseEmitter.onError(ex -> {
      log.info("SSE connection error: {}", ex.getMessage());
      removeEmitter(userId);
    });

    emitters.put(userId, sseEmitter);
    return sseEmitter;
  }

  private void removeEmitter(String userId) {
    emitters.remove(userId);
  }
}
