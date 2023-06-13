package com.hhkbdev.springsse.sse;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
public class SseService {

  private final SseEmitter sseEmitter;

  public SseService() {
    this.sseEmitter = new SseEmitter(Long.MAX_VALUE);
    sseEmitter.onCompletion(() -> {
      System.out.println("SSE connection completed");
    });

    sseEmitter.onTimeout(() -> {
      System.out.println("SSE connection timeout");
      sseEmitter.complete();
    });

    sseEmitter.onError((ex) -> {
      System.out.println("SSE connection error: " + ex.getMessage());
      sseEmitter.completeWithError(ex);
    });
  }

  public SseEmitter getSseEmitter() {
    return sseEmitter;
  }

  public void sendEventData(String id, Object data) {
    try {
      SseEmitter.SseEventBuilder event = SseEmitter.event()
          .id(id)
          .data(data);
      sseEmitter.send(event);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
