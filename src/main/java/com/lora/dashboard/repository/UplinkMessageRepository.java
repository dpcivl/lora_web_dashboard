package com.lora.dashboard.repository;

import com.lora.dashboard.entity.UplinkMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UplinkMessageRepository extends JpaRepository<UplinkMessage, Long> {
    
    // 디바이스별 최근 메시지 조회
    Page<UplinkMessage> findByDeviceIdOrderByTimestampDesc(String deviceId, Pageable pageable);
    
    // 애플리케이션별 메시지 조회
    Page<UplinkMessage> findByApplicationIdOrderByTimestampDesc(String applicationId, Pageable pageable);
    
    
    // 최근 메시지 조회 (전체)
    Page<UplinkMessage> findAllByOrderByTimestampDesc(Pageable pageable);
    
    
    
    
    // 특정 디바이스의 최근 메시지
    UplinkMessage findFirstByDeviceIdOrderByTimestampDesc(String deviceId);
    
    // 디바이스별 메시지 수 계산
    Long countByDeviceId(String deviceId);
    
    // 모든 디바이스 ID 목록
    @Query("SELECT DISTINCT m.deviceId FROM UplinkMessage m")
    List<String> findDistinctDeviceIds();
    
}