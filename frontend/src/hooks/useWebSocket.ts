import { useState, useEffect, useRef } from 'react';
import { UplinkMessage } from '../types';

interface UseWebSocketProps {
  url: string;
  onMessage?: (message: UplinkMessage) => void;
  onConnect?: () => void;
  onDisconnect?: () => void;
  onError?: (error: Event) => void;
}

interface UseWebSocketReturn {
  isConnected: boolean;
  lastMessage: UplinkMessage | null;
  error: string | null;
  connect: () => void;
  disconnect: () => void;
}

export const useWebSocket = ({
  url,
  onMessage,
  onConnect,
  onDisconnect,
  onError
}: UseWebSocketProps): UseWebSocketReturn => {
  const [isConnected, setIsConnected] = useState(false);
  const [lastMessage, setLastMessage] = useState<UplinkMessage | null>(null);
  const [error, setError] = useState<string | null>(null);
  const wsRef = useRef<WebSocket | null>(null);
  const reconnectTimerRef = useRef<NodeJS.Timeout | null>(null);
  const reconnectAttempts = useRef(0);
  const maxReconnectAttempts = 5;

  const connect = () => {
    if (wsRef.current?.readyState === WebSocket.OPEN) {
      return;
    }

    try {
      const ws = new WebSocket(url);
      wsRef.current = ws;

      ws.onopen = () => {
        console.log('WebSocket 연결됨');
        setIsConnected(true);
        setError(null);
        reconnectAttempts.current = 0;
        onConnect?.();
      };

      ws.onmessage = (event) => {
        try {
          const data = JSON.parse(event.data);
          
          // 연결 확인 메시지는 무시
          if (data.type === 'connected') {
            console.log('WebSocket 연결 확인:', data.message);
            return;
          }

          // UplinkMessage로 파싱
          const message: UplinkMessage = data;
          setLastMessage(message);
          onMessage?.(message);
        } catch (error) {
          console.error('WebSocket 메시지 파싱 오류:', error);
        }
      };

      ws.onclose = () => {
        console.log('WebSocket 연결 종료됨');
        setIsConnected(false);
        wsRef.current = null;
        onDisconnect?.();

        // 자동 재연결 시도
        if (reconnectAttempts.current < maxReconnectAttempts) {
          const delay = Math.pow(2, reconnectAttempts.current) * 1000; // 지수 백오프
          reconnectAttempts.current++;
          
          console.log(`${delay}ms 후 재연결 시도 (${reconnectAttempts.current}/${maxReconnectAttempts})`);
          
          reconnectTimerRef.current = setTimeout(() => {
            connect();
          }, delay);
        } else {
          setError('최대 재연결 시도 횟수를 초과했습니다.');
        }
      };

      ws.onerror = (event) => {
        console.error('WebSocket 오류:', event);
        setError('WebSocket 연결 오류가 발생했습니다.');
        onError?.(event);
      };

    } catch (error) {
      console.error('WebSocket 연결 실패:', error);
      setError('WebSocket 연결에 실패했습니다.');
    }
  };

  const disconnect = () => {
    if (reconnectTimerRef.current) {
      clearTimeout(reconnectTimerRef.current);
      reconnectTimerRef.current = null;
    }

    if (wsRef.current) {
      wsRef.current.close();
      wsRef.current = null;
    }

    setIsConnected(false);
    reconnectAttempts.current = maxReconnectAttempts; // 재연결 방지
  };

  useEffect(() => {
    connect();

    return () => {
      disconnect();
    };
  }, [url]);

  return {
    isConnected,
    lastMessage,
    error,
    connect,
    disconnect
  };
};