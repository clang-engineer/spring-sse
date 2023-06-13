package com.hhkbdev.springsse.sse;

import java.util.Map;
import java.util.concurrent.Executors;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/sse")
public class SseController {

  private final SseService sseService;

  public SseController(SseService sseService) {
    this.sseService = sseService;
  }

  @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public SseEmitter subscribe() {
    SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);

    Executors.newSingleThreadExecutor().execute(() -> {
      try {
        for (int i = 0; i < 1000; i++) {
          Map data = Map.of("id", i, "message", "Hello " + i);
          emitter.send(SseEmitter.event()
              .id(String.valueOf(i))
              .data(data));
          Thread.sleep(1000);
        }
        emitter.complete();
      } catch (Exception e) {
        emitter.completeWithError(e);
      }
    });

    return emitter;
  }

}
