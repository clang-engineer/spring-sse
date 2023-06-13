package com.hhkbdev.springsse.sse;

import java.util.Map;
import java.util.concurrent.Executors;
import org.slf4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/sse")
public class SseController {

  private final Logger log = org.slf4j.LoggerFactory.getLogger(SseController.class);

  private final SseService sseService;

  public SseController(SseService sseService) {
    this.sseService = sseService;
  }

  @GetMapping(value = "/subscribe/{userId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public SseEmitter subscribe(@PathVariable String userId) {
    log.info("SSE connection opened");
    return sseService.getSseEmitterByUserId(userId);
  }

  @GetMapping("/send/{userId}")
  public void sendEventData(@PathVariable String userId) {
    Executors.newSingleThreadExecutor().execute(() -> {
      try {
        for (int i = 0; i < 1000; i++) {
          Map<String, Object> data = Map.of("id", i, "message", "Hello " + i);
          sseService.sendEventData(userId, data);
          Thread.sleep(1000);
        }
      } catch (InterruptedException e) {
        log.error("Error while sending event data: {}", e.getMessage());
        Thread.currentThread().interrupt();
      }
    });
  }
}
