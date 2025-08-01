package com.lora.dashboard.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lora.dashboard.entity.UplinkMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
public class MessageWebSocketHandler implements WebSocketHandler {

    private static final Logger logger = LoggerFactory.getLogger(MessageWebSocketHandler.class);
    
    private final Set<WebSocketSession> sessions = new CopyOnWriteArraySet<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
        logger.info("WebSocket 연결 설정됨: {}", session.getId());
        
        // 연결 확인 메시지 전송
        session.sendMessage(new TextMessage("{\"type\":\"connected\",\"message\":\"WebSocket 연결 성공\"}"));
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        logger.debug("WebSocket 메시지 수신: {}", message.getPayload());
        // 클라이언트에서 온 메시지 처리 (필요시)
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        logger.error("WebSocket 전송 오류: {}", exception.getMessage());
        sessions.remove(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        sessions.remove(session);
        logger.info("WebSocket 연결 종료됨: {} - {}", session.getId(), closeStatus);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    /**
     * 모든 연결된 클라이언트에게 새로운 메시지를 브로드캐스트
     */
    public void broadcastMessage(UplinkMessage message) {
        if (sessions.isEmpty()) {
            return;
        }

        try {
            String jsonMessage = objectMapper.writeValueAsString(message);
            TextMessage textMessage = new TextMessage(jsonMessage);
            
            // 연결이 끊어진 세션을 제거하면서 메시지 전송
            sessions.removeIf(session -> {
                try {
                    if (session.isOpen()) {
                        session.sendMessage(textMessage);
                        return false; // 세션 유지
                    } else {
                        return true; // 세션 제거
                    }
                } catch (IOException e) {
                    logger.warn("메시지 전송 실패: {}", e.getMessage());
                    return true; // 세션 제거
                }
            });
            
            logger.debug("메시지를 {}개 세션에 브로드캐스트", sessions.size());
            
        } catch (Exception e) {
            logger.error("메시지 브로드캐스트 실패: {}", e.getMessage());
        }
    }

    /**
     * 연결된 세션 수 반환
     */
    public int getSessionCount() {
        return sessions.size();
    }
}