import React, { useState, useEffect } from 'react';
import { messageAPI } from '../api/client';
import { UplinkMessage, ApiResponse, SignalQuality } from '../types';
import ApplicationList from './ApplicationList';

interface MessageListProps {
  applicationId?: string;
}

const MessageList: React.FC<MessageListProps> = ({ applicationId }) => {
  const [messages, setMessages] = useState<UplinkMessage[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [selectedApplicationId, setSelectedApplicationId] = useState<string | null>(applicationId || null);

  useEffect(() => {
    if (selectedApplicationId) {
      fetchMessages();
    }
  }, [page, selectedApplicationId]);

  const fetchMessages = async () => {
    if (!selectedApplicationId) return;
    
    try {
      setLoading(true);
      const response = await messageAPI.getApplicationMessages(selectedApplicationId, page, 20);
      const data: ApiResponse<UplinkMessage> = response.data;
      
      setMessages(data.content);
      setTotalPages(data.totalPages);
      setTotalElements(data.totalElements);
      setError(null);
    } catch (error) {
      console.error('메시지 조회 실패:', error);
      setError('메시지를 불러올 수 없습니다.');
    } finally {
      setLoading(false);
    }
  };

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
    return new Date(timestamp).toLocaleString('ko-KR');
  };

  const handleApplicationSelect = (appId: string) => {
    setSelectedApplicationId(appId);
    setPage(0); // 새 애플리케이션 선택시 첫 페이지로
  };

  const handleBackToApplications = () => {
    setSelectedApplicationId(null);
    setMessages([]);
    setPage(0);
  };

  if (!selectedApplicationId) {
    return (
      <ApplicationList 
        onApplicationSelect={handleApplicationSelect}
        title="메시지 조회할 애플리케이션 선택"
      />
    );
  }

  if (loading) {
    return <div className="loading">메시지를 불러오는 중...</div>;
  }

  if (error) {
    return <div className="error">{error}</div>;
  }

  return (
    <div className="message-list">
      <div className="card">
        <div className="card-header">
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <h2>애플리케이션 {selectedApplicationId} - LoRa 메시지 ({totalElements?.toLocaleString() || 0}개)</h2>
            <button 
              onClick={handleBackToApplications}
              style={{
                padding: '0.5rem 1rem',
                backgroundColor: '#6c757d',
                color: 'white',
                border: 'none',
                borderRadius: '4px',
                cursor: 'pointer'
              }}
            >
              ← 애플리케이션 목록으로
            </button>
          </div>
        </div>
        <div className="card-body">
          <table className="table">
            <thead>
              <tr>
                <th>시간</th>
                <th>디바이스 ID</th>
                <th>애플리케이션 ID</th>
                <th>메시지</th>
                <th>RSSI</th>
                <th>SNR</th>
                <th>신호 품질</th>
                <th>프레임 수</th>
              </tr>
            </thead>
            <tbody>
              {messages?.map(message => (
                <tr key={message.id}>
                  <td>{formatTimestamp(message.timestamp)}</td>
                  <td>
                    <code>{message.deviceId}</code>
                  </td>
                  <td>{message.applicationId}</td>
                  <td>
                    {message.payloadText || (
                      <span style={{ color: '#6c757d', fontStyle: 'italic' }}>
                        [바이너리 데이터]
                      </span>
                    )}
                  </td>
                  <td>{message.rssi ? `${message.rssi} dBm` : 'N/A'}</td>
                  <td>{message.snr ? `${message.snr} dB` : 'N/A'}</td>
                  <td>{formatSignalQuality(message.signalQuality)}</td>
                  <td>{message.frameCount}</td>
                </tr>
              ))}
            </tbody>
          </table>

          {(!messages || messages.length === 0) && (
            <div style={{ textAlign: 'center', padding: '2rem', color: '#6c757d' }}>
              메시지가 없습니다.
            </div>
          )}

          {/* 페이지네이션 */}
          <div className="pagination">
            <button 
              disabled={page === 0} 
              onClick={() => setPage(page - 1)}>
              이전
            </button>
            <span>
              {page + 1} / {totalPages} 페이지
            </span>
            <button 
              disabled={page >= totalPages - 1} 
              onClick={() => setPage(page + 1)}>
              다음
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default MessageList;