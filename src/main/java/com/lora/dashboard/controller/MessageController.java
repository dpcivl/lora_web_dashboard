package com.lora.dashboard.controller;

import com.lora.dashboard.dto.StatisticsDto;
import com.lora.dashboard.dto.UplinkMessageDto;
import com.lora.dashboard.entity.UplinkMessage;
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
@RequestMapping("/messages")
@CrossOrigin(origins = "*")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @GetMapping("/recent")
    public ResponseEntity<Page<UplinkMessageDto>> getRecentMessages(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<UplinkMessageDto> messages = messageService.getAllMessagesDto(pageable);
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/device/{deviceId}")
    public ResponseEntity<Page<UplinkMessageDto>> getDeviceMessages(
            @PathVariable String deviceId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<UplinkMessageDto> messages = messageService.getMessagesByDeviceDto(deviceId, pageable);
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/application/{applicationId}")
    public ResponseEntity<Page<UplinkMessage>> getApplicationMessages(
            @PathVariable String applicationId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<UplinkMessage> messages = messageService.getMessagesByApplication(applicationId, pageable);
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/range")
    public ResponseEntity<List<UplinkMessage>> getMessagesInRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        
        List<UplinkMessage> messages = messageService.getMessagesInTimeRange(startTime, endTime);
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/device/{deviceId}/latest")
    public ResponseEntity<UplinkMessage> getLatestDeviceMessage(@PathVariable String deviceId) {
        UplinkMessage message = messageService.getLatestMessageByDevice(deviceId);
        if (message != null) {
            return ResponseEntity.ok(message);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/statistics")
    public ResponseEntity<StatisticsDto> getStatistics() {
        StatisticsDto stats = messageService.getStatistics();
        return ResponseEntity.ok(stats);
    }
}