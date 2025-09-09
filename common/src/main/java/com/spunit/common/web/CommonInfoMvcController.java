package com.spunit.common.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestController
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class CommonInfoMvcController {

    @Value("${spring.application.name:unknown}")
    private String appName;

    @Value("${info.version:${project.version:0.0.0}}")
    private String version;

    @GetMapping(value = "/api/info", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> info() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("name", appName);
        payload.put("version", version);
        payload.put("timestamp", Instant.now().toString());
        return payload;
    }
}
