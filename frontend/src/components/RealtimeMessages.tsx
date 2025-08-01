import React, { useState, useEffect } from 'react';
import { useWebSocket } from '../hooks/useWebSocket';
import { UplinkMessage, SignalQuality } from '../types';

const RealtimeMessages: React.FC = () => {
  const [realtimeMessages, setRealtimeMessages] = useState<UplinkMessage[]>([]);
  const maxMessages = 50;

  const wsUrl = process.env.REACT_APP_WS_URL || 'ws://localhost:8081/ws/messages';

  const { isConnected, lastMessage, error } = useWebSocket({
    url: wsUrl,
    onMessage: (message: UplinkMessage) => {
      setRealtimeMessages(prev => {
        const newMessages = [message, ...prev];
        return newMessages.slice(0, maxMessages); // 최근 50개만 유지
      });
    },
    onConnect: () => {
      console.log('실시간 메시지 스트림 연결됨');
    },
    onDisconnect: () => {
      console.log('실시간 메시지 스트림 연결 종료됨');
    }
  });

  const formatSignalQuality = (quality: SignalQuality) => {
    const classMap = {
      [SignalQuality.EXCELLENT]: 'signal-excellent',
      [SignalQuality.GOOD]: 'signal-good',
      [SignalQuality.FAIR]: 'signal-fair',
      [SignalQuality.POOR]: 'signal-poor'
    };

    const textMap = {
      [SignalQuality.EXCELLENT]: '매우 좋음',
      [SignalQuality.GOOD]: '좋음',
      [SignalQuality.FAIR]: '보통',
      [SignalQuality.POOR]: '나쁨'
    };

    return (
      <span className={classMap[quality]}>
        {textMap[quality]}
      </span>
    );
  };

  const formatTimestamp = (timestamp: string) => {
    return new Date(timestamp).toLocaleTimeString('ko-KR');
  };

  return (
    <div className="card">
      <div className="card-header">
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <span>실시간 메시지 ({realtimeMessages.length})</span>
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
            <div 
              style={{
                width: '8px',
                height: '8px',
                borderRadius: '50%',
                backgroundColor: isConnected ? '#28a745' : '#dc3545'
              }}
            />
            <span style={{ fontSize: '0.85em', color: '#6c757d' }}>
              {isConnected ? '연결됨' : '연결 끊김'}
            </span>
          </div>
        </div>
      </div>
      <div className="card-body">
        {error && (
          <div className="error" style={{ marginBottom: '1rem' }}>
            {error}
          </div>
        )}

        {realtimeMessages.length === 0 ? (
          <div style={{ textAlign: 'center', padding: '2rem', color: '#6c757d' }}>
            {isConnected ? '새로운 메시지를 기다리는 중...' : 'WebSocket에 연결하는 중...'}
          </div>
        ) : (
          <div 
            style={{ 
              maxHeight: '400px', 
              overflowY: 'auto',
              border: '1px solid #dee2e6',
              borderRadius: '4px'
            }}
          >
            <table className="table" style={{ margin: 0 }}>
              <thead style={{ position: 'sticky', top: 0, backgroundColor: '#f8f9fa', zIndex: 1 }}>
                <tr>
                  <th>시간</th>
                  <th>디바이스</th>
                  <th>메시지</th>
                  <th>RSSI</th>
                  <th>SNR</th>
                  <th>품질</th>
                </tr>
              </thead>
              <tbody>
                {realtimeMessages.map((message, index) => (
                  <tr 
                    key={`${message.id}-${index}`}
                    style={{
                      backgroundColor: index === 0 ? '#fff3cd' : 'transparent', // 최신 메시지 하이라이트
                      transition: 'background-color 2s ease-out'
                    }}
                  >
                    <td>{formatTimestamp(message.timestamp)}</td>
                    <td>
                      <code style={{ fontSize: '0.85em' }}>{message.deviceId}</code>
                    </td>
                    <td>
                      {message.payloadText || (
                        <span style={{ color: '#6c757d', fontStyle: 'italic' }}>
                          [바이너리]
                        </span>
                      )}
                    </td>
                    <td>{message.rssi} dBm</td>
                    <td>{message.snr} dB</td>
                    <td>{formatSignalQuality(message.signalQuality)}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}

        {lastMessage && (
          <div style={{ 
            marginTop: '1rem', 
            fontSize: '0.85em', 
            color: '#6c757d',
            textAlign: 'center'
          }}>
            마지막 업데이트: {formatTimestamp(lastMessage.timestamp)}
          </div>
        )}
      </div>
    </div>
  );
};

export default RealtimeMessages;