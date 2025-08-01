package com.lora.dashboard.repository;

import com.lora.dashboard.entity.JoinEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface JoinEventRepository extends JpaRepository<JoinEvent, Long> {
    
    // 디바이스별 JOIN 이벤트 조회
    Page<JoinEvent> findByDeviceIdOrderByTimestampDesc(String deviceId, Pageable pageable);
    
    // 애플리케이션별 JOIN 이벤트 조회
    Page<JoinEvent> findByApplicationIdOrderByTimestampDesc(String applicationId, Pageable pageable);
    
    // 최근 JOIN 이벤트 조회
    Page<JoinEvent> findAllByOrderByTimestampDesc(Pageable pageable);
    
    // 시간 범위별 JOIN 이벤트 조회
    List<JoinEvent> findByTimestampBetweenOrderByTimestampDesc(
        LocalDateTime startTime, LocalDateTime endTime);
    
    // DevEUI별 JOIN 이벤트 조회
    List<JoinEvent> findByDevEuiOrderByTimestampDesc(String devEui);
    
    // 최근 JOIN 이벤트 수
    @Query("SELECT COUNT(j) FROM JoinEvent j WHERE j.timestamp >= :since")
    Long countRecentJoinEvents(@Param("since") LocalDateTime since);
    
    // 디바이스별 JOIN 통계
    @Query("SELECT j.deviceId, COUNT(j) as count FROM JoinEvent j " +
           "WHERE j.timestamp >= :since GROUP BY j.deviceId ORDER BY count DESC")
    List<Object[]> getDeviceJoinCounts(@Param("since") LocalDateTime since);
    
    // 특정 디바이스의 최근 JOIN 이벤트
    JoinEvent findFirstByDeviceIdOrderByTimestampDesc(String deviceId);
}