package com.lora.dashboard.repository;

import com.lora.dashboard.entity.UplinkMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UplinkMessageRepository extends JpaRepository<UplinkMessage, Long> {
    
    // 디바이스별 최근 메시지 조회
    Page<UplinkMessage> findByDeviceIdOrderByTimestampDesc(String deviceId, Pageable pageable);
    
    // 애플리케이션별 메시지 조회
    Page<UplinkMessage> findByApplicationIdOrderByTimestampDesc(String applicationId, Pageable pageable);
    
    // 시간 범위별 조회
    List<UplinkMessage> findByTimestampBetweenOrderByTimestampDesc(
        LocalDateTime startTime, LocalDateTime endTime);
    
    // 최근 메시지 조회 (전체)
    Page<UplinkMessage> findAllByOrderByTimestampDesc(Pageable pageable);
    
    // 디바이스 통계
    @Query("SELECT m.deviceId, COUNT(m) as count FROM UplinkMessage m " +
           "WHERE m.timestamp >= :since GROUP BY m.deviceId ORDER BY count DESC")
    List<Object[]> getDeviceMessageCounts(@Param("since") LocalDateTime since);
    
    // 신호 품질별 통계
    @Query(value = "SELECT " +
           "COUNT(CASE WHEN rssi > -70 AND snr > 10 THEN 1 END) as excellent, " +
           "COUNT(CASE WHEN rssi > -85 AND snr > 5 AND (rssi <= -70 OR snr <= 10) THEN 1 END) as good, " +
           "COUNT(CASE WHEN rssi > -100 AND snr > 0 AND (rssi <= -85 OR snr <= 5) THEN 1 END) as fair, " +
           "COUNT(CASE WHEN rssi <= -100 OR snr <= 0 OR rssi IS NULL OR snr IS NULL THEN 1 END) as poor " +
           "FROM uplink_messages WHERE timestamp >= :since", nativeQuery = true)
    Object[] getSignalQualityStats(@Param("since") LocalDateTime since);
    
    // 활성 디바이스 수
    @Query("SELECT COUNT(DISTINCT m.deviceId) FROM UplinkMessage m WHERE m.timestamp >= :since")
    Long countActiveDevices(@Param("since") LocalDateTime since);
    
    // 특정 디바이스의 최근 메시지
    UplinkMessage findFirstByDeviceIdOrderByTimestampDesc(String deviceId);
    
    // 시간대별 메시지 수 (차트용)
    @Query(value = "SELECT strftime('%Y-%m-%d %H:00:00', timestamp) as hour, COUNT(*) as count " +
           "FROM uplink_messages WHERE timestamp >= :since " +
           "GROUP BY hour ORDER BY hour", nativeQuery = true)
    List<Object[]> getHourlyMessageCounts(@Param("since") LocalDateTime since);
}