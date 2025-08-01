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

    public StatisticsDto getStatistics() {
        LocalDateTime last24Hours = LocalDateTime.now().minusHours(24);
        LocalDateTime lastWeek = LocalDateTime.now().minusDays(7);

        StatisticsDto stats = new StatisticsDto();

        // 기본 통계
        stats.setTotalMessages(uplinkMessageRepository.count());
        stats.setLast24HourMessages(
            (long) uplinkMessageRepository.findByTimestampBetweenOrderByTimestampDesc(
                last24Hours, LocalDateTime.now()).size()
        );
        stats.setActiveDevices(uplinkMessageRepository.countActiveDevices(last24Hours));
        stats.setTotalJoinEvents(joinEventRepository.count());
        stats.setRecentJoinEvents(joinEventRepository.countRecentJoinEvents(last24Hours));

        // 디바이스별 메시지 수
        List<Object[]> deviceCounts = uplinkMessageRepository.getDeviceMessageCounts(lastWeek);
        stats.setDeviceCounts(
            deviceCounts.stream()
                .map(row -> new StatisticsDto.DeviceCountDto(
                    (String) row[0], 
                    ((Number) row[1]).longValue()
                ))
                .collect(Collectors.toList())
        );

        // 신호 품질 통계
        Object[] signalStats = uplinkMessageRepository.getSignalQualityStats(lastWeek);
        if (signalStats != null && signalStats.length >= 4) {
            stats.setSignalQuality(new StatisticsDto.SignalQualityStatsDto(
                ((Number) signalStats[0]).longValue(),
                ((Number) signalStats[1]).longValue(),
                ((Number) signalStats[2]).longValue(),
                ((Number) signalStats[3]).longValue()
            ));
        }

        // 시간별 메시지 수 (지난 24시간)
        List<Object[]> hourlyCounts = uplinkMessageRepository.getHourlyMessageCounts(last24Hours);
        stats.setHourlyCounts(
            hourlyCounts.stream()
                .map(row -> new StatisticsDto.HourlyCountDto(
                    (String) row[0],
                    ((Number) row[1]).longValue()
                ))
                .collect(Collectors.toList())
        );

        return stats;
    }

    public List<UplinkMessage> getMessagesInTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        return uplinkMessageRepository.findByTimestampBetweenOrderByTimestampDesc(startTime, endTime);
    }

    public UplinkMessage getLatestMessageByDevice(String deviceId) {
        return uplinkMessageRepository.findFirstByDeviceIdOrderByTimestampDesc(deviceId);
    }

    public JoinEvent getLatestJoinEventByDevice(String deviceId) {
        return joinEventRepository.findFirstByDeviceIdOrderByTimestampDesc(deviceId);
    }
}