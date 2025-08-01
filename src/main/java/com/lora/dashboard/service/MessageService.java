package com.lora.dashboard.service;

import com.lora.dashboard.dto.StatisticsDto;
import com.lora.dashboard.dto.UplinkMessageDto;
import com.lora.dashboard.entity.UplinkMessage;
import com.lora.dashboard.entity.JoinEvent;
import com.lora.dashboard.repository.UplinkMessageRepository;
import com.lora.dashboard.repository.JoinEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MessageService {

    @Autowired
    private UplinkMessageRepository uplinkMessageRepository;

    @Autowired
    private JoinEventRepository joinEventRepository;

    public Page<UplinkMessage> getAllMessages(Pageable pageable) {
        return uplinkMessageRepository.findAllByOrderByTimestampDesc(pageable);
    }

    public Page<UplinkMessageDto> getAllMessagesDto(Pageable pageable) {
        Page<UplinkMessage> messages = uplinkMessageRepository.findAllByOrderByTimestampDesc(pageable);
        List<UplinkMessageDto> dtoList = messages.getContent().stream()
                .map(UplinkMessageDto::new)
                .collect(Collectors.toList());
        return new PageImpl<>(dtoList, pageable, messages.getTotalElements());
    }

    public Page<UplinkMessage> getMessagesByDevice(String deviceId, Pageable pageable) {
        return uplinkMessageRepository.findByDeviceIdOrderByTimestampDesc(deviceId, pageable);
    }

    public Page<UplinkMessageDto> getMessagesByDeviceDto(String deviceId, Pageable pageable) {
        Page<UplinkMessage> messages = uplinkMessageRepository.findByDeviceIdOrderByTimestampDesc(deviceId, pageable);
        List<UplinkMessageDto> dtoList = messages.getContent().stream()
                .map(UplinkMessageDto::new)
                .collect(Collectors.toList());
        return new PageImpl<>(dtoList, pageable, messages.getTotalElements());
    }

    public Page<UplinkMessage> getMessagesByApplication(String applicationId, Pageable pageable) {
        return uplinkMessageRepository.findByApplicationIdOrderByTimestampDesc(applicationId, pageable);
    }

    public Page<JoinEvent> getAllJoinEvents(Pageable pageable) {
        return joinEventRepository.findAllByOrderByTimestampDesc(pageable);
    }

    public Page<JoinEvent> getJoinEventsByDevice(String deviceId, Pageable pageable) {
        return joinEventRepository.findByDeviceIdOrderByTimestampDesc(deviceId, pageable);
    }

    public StatisticsDto getStatistics() {
        StatisticsDto stats = new StatisticsDto();

        // 기본 통계
        stats.setTotalMessages(uplinkMessageRepository.count());
        stats.setTotalJoinEvents(joinEventRepository.count());
        
        // 24시간 내 메시지와 활성 디바이스 계산
        LocalDateTime last24Hours = LocalDateTime.now().minusHours(24);
        long last24HourMessages = calculateMessagesAfter(last24Hours);
        long activeDevices = calculateActiveDevicesAfter(last24Hours);
        
        stats.setLast24HourMessages(last24HourMessages);
        stats.setActiveDevices(activeDevices);
        stats.setRecentJoinEvents(0L); // JOIN 이벤트는 나중에 구현
        
        // 디바이스별 메시지 수 (간단 버전)
        java.util.List<StatisticsDto.DeviceCountDto> deviceCounts = new java.util.ArrayList<>();
        // 전체 디바이스 목록을 가져와서 각각의 메시지 수를 계산
        java.util.List<String> deviceIds = uplinkMessageRepository.findDistinctDeviceIds();
        for (String deviceId : deviceIds) {
            Long count = uplinkMessageRepository.countByDeviceId(deviceId);
            deviceCounts.add(new StatisticsDto.DeviceCountDto(deviceId, count));
        }
        // 메시지 많은 순으로 정렬
        deviceCounts.sort((a, b) -> Long.compare(b.getCount(), a.getCount()));
        stats.setDeviceCounts(deviceCounts);
        
        // 시간별 메시지 수 계산 (최근 24시간)
        java.util.List<StatisticsDto.HourlyCountDto> hourlyCounts = calculateHourlyMessageCounts(last24Hours);
        stats.setHourlyCounts(hourlyCounts);
        
        // 신호 품질 기본값
        StatisticsDto.SignalQualityStatsDto signalQuality = new StatisticsDto.SignalQualityStatsDto(0L, 0L, 0L, 0L);
        stats.setSignalQuality(signalQuality);

        return stats;
    }


    public UplinkMessage getLatestMessageByDevice(String deviceId) {
        return uplinkMessageRepository.findFirstByDeviceIdOrderByTimestampDesc(deviceId);
    }

    public JoinEvent getLatestJoinEventByDevice(String deviceId) {
        return joinEventRepository.findFirstByDeviceIdOrderByTimestampDesc(deviceId);
    }

    public Long getMessageCount() {
        return uplinkMessageRepository.count();
    }
    
    // String timestamp를 LocalDateTime으로 파싱하는 헬퍼 메서드
    private LocalDateTime parseTimestamp(String timestamp) {
        if (timestamp == null || timestamp.isEmpty()) {
            return null;
        }
        
        try {
            // "2025-08-01T01:11:09.845112" 형식 파싱
            return LocalDateTime.parse(timestamp, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (DateTimeParseException e) {
            // 파싱 실패시 null 반환
            return null;
        }
    }
    
    // 특정 시간 이후의 메시지 수 계산
    private long calculateMessagesAfter(LocalDateTime afterTime) {
        List<UplinkMessage> allMessages = uplinkMessageRepository.findAll();
        return allMessages.stream()
                .filter(msg -> {
                    LocalDateTime msgTime = parseTimestamp(msg.getTimestamp());
                    return msgTime != null && msgTime.isAfter(afterTime);
                })
                .count();
    }
    
    // 특정 시간 이후의 활성 디바이스 수 계산
    private long calculateActiveDevicesAfter(LocalDateTime afterTime) {
        List<UplinkMessage> allMessages = uplinkMessageRepository.findAll();
        return allMessages.stream()
                .filter(msg -> {
                    LocalDateTime msgTime = parseTimestamp(msg.getTimestamp());
                    return msgTime != null && msgTime.isAfter(afterTime);
                })
                .map(UplinkMessage::getDeviceId)
                .distinct()
                .count();
    }
    
    // 시간별 메시지 수 계산
    private java.util.List<StatisticsDto.HourlyCountDto> calculateHourlyMessageCounts(LocalDateTime afterTime) {
        List<UplinkMessage> allMessages = uplinkMessageRepository.findAll();
        
        // 시간별로 메시지를 그룹화
        Map<String, Long> hourlyMap = allMessages.stream()
                .filter(msg -> {
                    LocalDateTime msgTime = parseTimestamp(msg.getTimestamp());
                    return msgTime != null && msgTime.isAfter(afterTime);
                })
                .collect(Collectors.groupingBy(
                    msg -> {
                        LocalDateTime msgTime = parseTimestamp(msg.getTimestamp());
                        if (msgTime == null) return "unknown";
                        return msgTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:00:00"));
                    },
                    Collectors.counting()
                ));
        
        // 결과를 시간 순으로 정렬하여 반환
        return hourlyMap.entrySet().stream()
                .map(entry -> new StatisticsDto.HourlyCountDto(entry.getKey(), entry.getValue()))
                .sorted((a, b) -> a.getHour().compareTo(b.getHour()))
                .collect(Collectors.toList());
    }
}