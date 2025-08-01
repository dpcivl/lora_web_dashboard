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

import java.util.List;
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

    public Page<JoinEvent> getJoinEventsByApplication(String applicationId, Pageable pageable) {
        return joinEventRepository.findByApplicationIdOrderByTimestampDesc(applicationId, pageable);
    }

    public StatisticsDto getStatistics() {
        StatisticsDto stats = new StatisticsDto();

        // 기본 통계
        stats.setTotalMessages(uplinkMessageRepository.count());
        stats.setTotalJoinEvents(joinEventRepository.count());
        
        // 간단한 통계 
        stats.setLast24HourMessages(0L);
        stats.setActiveDevices(1L); // 현재 1개 디바이스 활성
        
        // JOIN 이벤트는 적으므로 실제 계산 (최근 24시간)
        long recentJoinEvents = calculateRecentJoinEvents();
        stats.setRecentJoinEvents(recentJoinEvents);
        
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
        
        // 시간별 메시지 수 (최근 12시간만)
        java.util.List<StatisticsDto.HourlyCountDto> hourlyCounts = calculateSimpleHourlyCounts();
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

    public List<String> getAllApplicationIds() {
        return uplinkMessageRepository.findDistinctApplicationIds();
    }

    public List<String> getAllJoinEventApplicationIds() {
        return joinEventRepository.findDistinctApplicationIds();
    }
    
    // 최근 JOIN 이벤트 계산 (24시간 내)
    private long calculateRecentJoinEvents() {
        try {
            // JOIN 이벤트가 적으므로 모두 가져와서 확인
            List<JoinEvent> allJoinEvents = joinEventRepository.findAll();
            
            // KST 현재 시간에서 24시간 전
            java.time.ZonedDateTime nowKst = java.time.ZonedDateTime.now(java.time.ZoneId.of("Asia/Seoul"));
            java.time.ZonedDateTime yesterdayKst = nowKst.minusHours(24);
            
            long count = 0;
            for (JoinEvent event : allJoinEvents) {
                String timestampString = event.getTimestamp();
                if (timestampString != null && !timestampString.isEmpty()) {
                    try {
                        // "2025-08-01T00:45:09.480899" 형식 파싱
                        java.time.LocalDateTime eventTime = java.time.LocalDateTime.parse(
                            timestampString, 
                            java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME
                        );
                        
                        // UTC를 KST로 변환하여 비교
                        java.time.ZonedDateTime utcEventTime = eventTime.atZone(java.time.ZoneId.of("UTC"));
                        java.time.ZonedDateTime kstEventTime = utcEventTime.withZoneSameInstant(java.time.ZoneId.of("Asia/Seoul"));
                        
                        if (kstEventTime.isAfter(yesterdayKst)) {
                            count++;
                        }
                    } catch (Exception e) {
                        // 파싱 실패시 무시
                    }
                }
            }
            
            return count;
        } catch (Exception e) {
            // 에러 발생시 0 반환
            return 0L;
        }
    }
    
    // 시간별 메시지 수 계산 (최근 24시간)
    private java.util.List<StatisticsDto.HourlyCountDto> calculateSimpleHourlyCounts() {
        try {
            // KST 현재 시간 기준으로 24시간 전부터 시작
            java.time.ZonedDateTime nowKst = java.time.ZonedDateTime.now(java.time.ZoneId.of("Asia/Seoul"));
            java.time.ZonedDateTime startTime = nowKst.minusHours(23).withMinute(0).withSecond(0).withNano(0);
            
            // 24시간 동안의 모든 시간대를 미리 생성 (0으로 초기화)
            java.util.Map<String, Long> hourlyMap = new java.util.LinkedHashMap<>();
            for (int i = 0; i < 24; i++) {
                java.time.ZonedDateTime hourTime = startTime.plusHours(i);
                String hourKey = hourTime.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:00"));
                hourlyMap.put(hourKey, 0L);
            }
            
            // 모든 메시지를 가져와서 최근 24시간 내의 메시지만 필터링
            List<UplinkMessage> allMessages = uplinkMessageRepository.findAll();
            
            for (UplinkMessage message : allMessages) {
                String timestampString = message.getTimestamp();
                if (timestampString != null && !timestampString.isEmpty()) {
                    try {
                        java.time.LocalDateTime msgTime = java.time.LocalDateTime.parse(
                            timestampString, 
                            java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME
                        );
                        
                        // UTC를 KST(+9시간)로 변환
                        java.time.ZonedDateTime utcTime = msgTime.atZone(java.time.ZoneId.of("UTC"));
                        java.time.ZonedDateTime kstTime = utcTime.withZoneSameInstant(java.time.ZoneId.of("Asia/Seoul"));
                        
                        // 최근 24시간 내의 메시지만 처리
                        if (kstTime.isAfter(startTime.minusHours(1)) && kstTime.isBefore(nowKst.plusHours(1))) {
                            String hourKey = kstTime.format(
                                java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:00")
                            );
                            
                            if (hourlyMap.containsKey(hourKey)) {
                                hourlyMap.put(hourKey, hourlyMap.get(hourKey) + 1);
                            }
                        }
                    } catch (Exception e) {
                        // 파싱 실패시 무시
                    }
                }
            }
            
            // 결과를 리스트로 변환 (시간 순서대로 정렬됨)
            return hourlyMap.entrySet().stream()
                .map(entry -> new StatisticsDto.HourlyCountDto(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
                
        } catch (Exception e) {
            // 에러 발생시 빈 리스트 반환
            return new java.util.ArrayList<>();
        }
    }
    
}