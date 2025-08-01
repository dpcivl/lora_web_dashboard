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

import java.util.List;

@Service
@EnableAsync
public class RealtimeService {

    private static final Logger logger = LoggerFactory.getLogger(RealtimeService.class);

    @Autowired
    private UplinkMessageRepository uplinkMessageRepository;

    @Autowired
    private MessageWebSocketHandler webSocketHandler;


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
     * 현재 timestamp를 String으로 변경했으므로 이 기능은 임시로 비활성화
     */
    // @Scheduled(fixedRate = 5000) // 5초마다 실행
    public void checkForNewMessages() {
        // 현재 비활성화됨 - timestamp 필드가 String으로 변경되어 시간 비교가 복잡함
        logger.debug("실시간 메시지 확인 기능은 현재 비활성화됨");
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