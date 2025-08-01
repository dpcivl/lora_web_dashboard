package com.lora.dashboard.controller;

import com.lora.dashboard.entity.JoinEvent;
import com.lora.dashboard.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/join-events")
@CrossOrigin(origins = "*")
public class JoinEventController {

    @Autowired
    private MessageService messageService;

    @GetMapping("/recent")
    public ResponseEntity<Page<JoinEvent>> getRecentJoinEvents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<JoinEvent> joinEvents = messageService.getAllJoinEvents(pageable);
        return ResponseEntity.ok(joinEvents);
    }

    @GetMapping("/device/{deviceId}")
    public ResponseEntity<Page<JoinEvent>> getDeviceJoinEvents(
            @PathVariable String deviceId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<JoinEvent> joinEvents = messageService.getJoinEventsByDevice(deviceId, pageable);
        return ResponseEntity.ok(joinEvents);
    }

    @GetMapping("/device/{deviceId}/latest")
    public ResponseEntity<JoinEvent> getLatestDeviceJoinEvent(@PathVariable String deviceId) {
        JoinEvent joinEvent = messageService.getLatestJoinEventByDevice(deviceId);
        if (joinEvent != null) {
            return ResponseEntity.ok(joinEvent);
        }
        return ResponseEntity.notFound().build();
    }
}