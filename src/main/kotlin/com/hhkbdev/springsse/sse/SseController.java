package com.hhkbdev.springsse.sse;

import java.util.Map;
import java.util.concurrent.Executors;
import org.slf4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
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

  @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public SseEmitter subscribe() {
    log.info("SSE connection opened");
    return sseService.getSseEmitter();
  }

  @GetMapping("/send")
  public void sendEventData() {
    Executors.newSingleThreadExecutor().execute(() -> {
      try {
        for (int i = 0; i < 1000; i++) {
          Map data = Map.of("id", i, "message", "Hello " + i);
          sseService.sendEventData(String.valueOf(i), data);
          Thread.sleep(1000);
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    });
  }
}
