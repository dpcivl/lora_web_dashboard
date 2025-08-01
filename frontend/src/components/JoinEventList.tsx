import React, { useState, useEffect } from 'react';
import { joinEventAPI } from '../api/client';
import { JoinEvent, ApiResponse, SignalQuality } from '../types';
import ApplicationList from './ApplicationList';

interface JoinEventListProps {
  applicationId?: string;
}

const JoinEventList: React.FC<JoinEventListProps> = ({ applicationId }) => {
  const [joinEvents, setJoinEvents] = useState<JoinEvent[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [selectedApplicationId, setSelectedApplicationId] = useState<string | null>(applicationId || null);

  useEffect(() => {
    if (selectedApplicationId) {
      fetchJoinEvents();
    }
  }, [page, selectedApplicationId]);

  const fetchJoinEvents = async () => {
    if (!selectedApplicationId) return;
    
    try {
      setLoading(true);
      const response = await joinEventAPI.getApplicationJoinEvents(selectedApplicationId, page, 20);
      const data: ApiResponse<JoinEvent> = response.data;
      
      setJoinEvents(data.content);
      setTotalPages(data.totalPages);
      setTotalElements(data.totalElements);
      setError(null);
    } catch (error) {
      console.error('JOIN 이벤트 조회 실패:', error);
      setError('JOIN 이벤트를 불러올 수 없습니다.');
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
    setJoinEvents([]);
    setPage(0);
  };

  if (!selectedApplicationId) {
    return (
      <ApplicationList 
        onApplicationSelect={handleApplicationSelect}
        title="JOIN 이벤트 조회할 애플리케이션 선택"
        type="join-event"
      />
    );
  }

  if (loading) {
    return <div className="loading">JOIN 이벤트를 불러오는 중...</div>;
  }

  if (error) {
    return <div className="error">{error}</div>;
  }

  return (
    <div className="join-event-list">
      <div className="card">
        <div className="card-header">
          <div style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
            <button 
              onClick={handleBackToApplications}
              className="back-button"
              style={{ 
                padding: '0.5rem 1rem', 
                border: '1px solid #ddd', 
                borderRadius: '4px',
                background: '#f8f9fa',
                cursor: 'pointer'
              }}
            >
              ← 애플리케이션 목록
            </button>
            <h2>애플리케이션 "{selectedApplicationId}" JOIN 이벤트 ({totalElements?.toLocaleString() || 0}개)</h2>
          </div>
        </div>
        <div className="card-body">
          <table className="table">
            <thead>
              <tr>
                <th>시간</th>
                <th>디바이스 ID</th>
                <th>DevEUI</th>
                <th>JoinEUI</th>
                <th>DevAddr</th>
                <th>RSSI</th>
                <th>SNR</th>
                <th>신호 품질</th>
              </tr>
            </thead>
            <tbody>
              {joinEvents?.map(event => (
                <tr key={event.id}>
                  <td>{formatTimestamp(event.timestamp)}</td>
                  <td>
                    <code>{event.deviceId}</code>
                  </td>
                  <td>
                    <code style={{ fontSize: '0.85em' }}>{event.devEui}</code>
                  </td>
                  <td>
                    <code style={{ fontSize: '0.85em' }}>{event.joinEui || 'N/A'}</code>
                  </td>
                  <td>
                    <code style={{ fontSize: '0.85em' }}>{event.devAddr || 'N/A'}</code>
                  </td>
                  <td>{event.rssi ? `${event.rssi} dBm` : 'N/A'}</td>
                  <td>{event.snr ? `${event.snr} dB` : 'N/A'}</td>
                  <td>{formatSignalQuality(event.signalQuality)}</td>
                </tr>
              ))}
            </tbody>
          </table>

          {(!joinEvents || joinEvents.length === 0) && (
            <div style={{ textAlign: 'center', padding: '2rem', color: '#6c757d' }}>
              JOIN 이벤트가 없습니다.
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

export default JoinEventList;