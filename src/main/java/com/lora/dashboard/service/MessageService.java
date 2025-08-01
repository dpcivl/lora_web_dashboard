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

    public StatisticsDto getStatistics() {
        StatisticsDto stats = new StatisticsDto();

        // 기본 통계 (간단한 버전)
        stats.setTotalMessages(uplinkMessageRepository.count());
        stats.setTotalJoinEvents(joinEventRepository.count());

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
}