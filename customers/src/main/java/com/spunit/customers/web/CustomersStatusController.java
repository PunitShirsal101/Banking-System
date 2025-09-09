package com.spunit.customers.web;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(path = "/api/customers", produces = MediaType.APPLICATION_JSON_VALUE)
public class CustomersStatusController {

    @GetMapping("/status")
    public Mono<ResponseEntity<Map<String, Object>>> status() {
        Map<String, Object> body = new HashMap<>();
        body.put("service", "customers");
        body.put("status", "ok");
        return Mono.just(ResponseEntity.ok(body));
    }
}
