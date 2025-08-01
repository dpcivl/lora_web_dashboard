package com.lora.dashboard.repository;

import com.lora.dashboard.entity.JoinEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JoinEventRepository extends JpaRepository<JoinEvent, Long> {
    
    // 디바이스별 JOIN 이벤트 조회
    Page<JoinEvent> findByDeviceIdOrderByTimestampDesc(String deviceId, Pageable pageable);
    
    // 애플리케이션별 JOIN 이벤트 조회
    Page<JoinEvent> findByApplicationIdOrderByTimestampDesc(String applicationId, Pageable pageable);
    
    // 최근 JOIN 이벤트 조회
    Page<JoinEvent> findAllByOrderByTimestampDesc(Pageable pageable);
    
    
    // DevEUI별 JOIN 이벤트 조회
    List<JoinEvent> findByDevEuiOrderByTimestampDesc(String devEui);
    
    
    
    // 특정 디바이스의 최근 JOIN 이벤트
    JoinEvent findFirstByDeviceIdOrderByTimestampDesc(String deviceId);
}