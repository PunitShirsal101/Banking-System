package com.spunit.transfers.web;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(path = "/api/transfers", produces = MediaType.APPLICATION_JSON_VALUE)
public class TransfersStatusController {

    @GetMapping("/status")
    public Mono<ResponseEntity<Map<String, Object>>> status() {
        Map<String, Object> body = new HashMap<>();
        body.put("service", "transfers");
        body.put("status", "ok");
        return Mono.just(ResponseEntity.ok(body));
    }
}
