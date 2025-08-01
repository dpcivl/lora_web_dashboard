package com.lora.dashboard.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/health")
@CrossOrigin(origins = "*")
public class HealthController {

    @GetMapping
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", LocalDateTime.now());
        health.put("service", "LoRa Web Dashboard API");
        health.put("version", "1.0.0");
        
        return ResponseEntity.ok(health);
    }

    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> info() {
        Map<String, Object> info = new HashMap<>();
        info.put("application", "LoRa Web Dashboard");
        info.put("description", "Web dashboard for LoRa Gateway Logger data visualization");
        info.put("version", "1.0.0");
        info.put("port", "8081");
        info.put("database", "SQLite");
        info.put("build-time", LocalDateTime.now());
        
        return ResponseEntity.ok(info);
    }
}