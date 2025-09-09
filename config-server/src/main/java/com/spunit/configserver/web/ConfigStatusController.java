package com.spunit.configserver.web;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(path = "/api/config", produces = MediaType.APPLICATION_JSON_VALUE)
public class ConfigStatusController {

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> status() {
        Map<String, Object> body = new HashMap<>();
        body.put("service", "config");
        body.put("status", "ok");
        return ResponseEntity.ok(body);
    }
}
