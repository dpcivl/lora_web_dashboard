package com.lora.dashboard.service;

import com.lora.dashboard.entity.UplinkMessage;
import com.lora.dashboard.repository.UplinkMessageRepository;
import com.lora.dashboard.websocket.MessageWebSocketHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@EnableAsync
public class RealtimeService {

    private static final Logger logger = LoggerFactory.getLogger(RealtimeService.class);

    @Autowired
    private UplinkMessageRepository uplinkMessageRepository;

    @Autowired
    private MessageWebSocketHandler webSocketHandler;

    private LocalDateTime lastBroadcastTime = LocalDateTime.now();

    /**
     * 새로운 메시지를 웹소켓으로 브로드캐스트
     */
    @Async("taskExecutor")
    public void broadcastNewMessage(UplinkMessage message) {
        try {
            webSocketHandler.broadcastMessage(message);
            logger.debug("실시간 메시지 브로드캐스트: {}", message.getDeviceId());
        } catch (Exception e) {
            logger.error("실시간 브로드캐스트 실패: {}", e.getMessage());
        }
    }

    /**
     * 주기적으로 새로운 메시지를 확인하여 브로드캐스트
     * 실제 환경에서는 LoRa Gateway Logger가 직접 API를 호출하거나
     * 데이터베이스 트리거를 사용하는 것이 더 효율적입니다.
     */
    @Scheduled(fixedRate = 5000) // 5초마다 실행
    public void checkForNewMessages() {
        try {
            // 연결된 WebSocket 세션이 없으면 스킵
            if (webSocketHandler.getSessionCount() == 0) {
                return;
            }

            // 마지막 브로드캐스트 이후의 새로운 메시지 조회
            List<UplinkMessage> newMessages = uplinkMessageRepository
                .findByTimestampBetweenOrderByTimestampDesc(lastBroadcastTime, LocalDateTime.now());

            if (!newMessages.isEmpty()) {
                logger.info("새로운 메시지 {}개 발견", newMessages.size());
                
                // 새로운 메시지들을 시간 순으로 브로드캐스트
                for (UplinkMessage message : newMessages) {
                    broadcastNewMessage(message);
                }
                
                // 마지막 브로드캐스트 시간 업데이트
                lastBroadcastTime = LocalDateTime.now();
            }

        } catch (Exception e) {
            logger.error("새 메시지 확인 중 오류: {}", e.getMessage());
        }
    }

    /**
     * WebSocket 연결 상태 로깅
     */
    @Scheduled(fixedRate = 60000) // 1분마다 실행
    public void logWebSocketStatus() {
        int sessionCount = webSocketHandler.getSessionCount();
        if (sessionCount > 0) {
            logger.info("활성 WebSocket 세션: {}개", sessionCount);
        }
    }
}