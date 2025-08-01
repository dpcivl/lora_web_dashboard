import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import { messageAPI, joinEventAPI } from '../api/client';
import { UplinkMessage, JoinEvent, ApiResponse, SignalQuality } from '../types';

const DeviceView: React.FC = () => {
  const { deviceId } = useParams<{ deviceId: string }>();
  const [messages, setMessages] = useState<UplinkMessage[]>([]);
  const [joinEvents, setJoinEvents] = useState<JoinEvent[]>([]);
  const [latestMessage, setLatestMessage] = useState<UplinkMessage | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [activeTab, setActiveTab] = useState<'messages' | 'joins'>('messages');

  useEffect(() => {
    if (deviceId) {
      fetchDeviceData();
    }
  }, [deviceId]);

  const fetchDeviceData = async () => {
    if (!deviceId) return;

    try {
      setLoading(true);
      
      // 병렬로 데이터 조회
      const [messagesResponse, joinEventsResponse, latestResponse] = await Promise.allSettled([
        messageAPI.getDeviceMessages(deviceId, 0, 50),
        joinEventAPI.getDeviceJoinEvents(deviceId, 0, 20),
        messageAPI.getLatestDeviceMessage(deviceId)
      ]);

      if (messagesResponse.status === 'fulfilled') {
        setMessages(messagesResponse.value.data.content);
      }

      if (joinEventsResponse.status === 'fulfilled') {
        setJoinEvents(joinEventsResponse.value.data.content);
      }

      if (latestResponse.status === 'fulfilled') {
        setLatestMessage(latestResponse.value.data);
      }

      setError(null);
    } catch (error) {
      console.error('디바이스 데이터 조회 실패:', error);
      setError('디바이스 데이터를 불러올 수 없습니다.');
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

  if (loading) {
    return <div className="loading">디바이스 데이터를 불러오는 중...</div>;
  }

  if (error) {
    return <div className="error">{error}</div>;
  }

  return (
    <div className="device-view">
      <h2>디바이스 상세정보: <code>{deviceId}</code></h2>

      {/* 디바이스 요약 정보 */}
      {latestMessage && (
        <div className="card" style={{ marginBottom: '1rem' }}>
          <div className="card-header">디바이스 현황</div>
          <div className="card-body">
            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))', gap: '1rem' }}>
              <div>
                <strong>마지막 활동:</strong><br />
                {formatTimestamp(latestMessage.timestamp)}
              </div>
              <div>
                <strong>DevEUI:</strong><br />
                <code>{latestMessage.devEui}</code>
              </div>
              <div>
                <strong>애플리케이션 ID:</strong><br />
                {latestMessage.applicationId}
              </div>
              <div>
                <strong>최근 신호 품질:</strong><br />
                {formatSignalQuality(latestMessage.signalQuality)}
              </div>
            </div>
          </div>
        </div>
      )}

      {/* 탭 네비게이션 */}
      <div style={{ marginBottom: '1rem' }}>
        <button
          style={{
            padding: '0.5rem 1rem',
            marginRight: '0.5rem',
            backgroundColor: activeTab === 'messages' ? '#007bff' : '#f8f9fa',
            color: activeTab === 'messages' ? 'white' : '#495057',
            border: '1px solid #dee2e6',
            borderRadius: '4px',
            cursor: 'pointer'
          }}
          onClick={() => setActiveTab('messages')}
        >
          메시지 ({messages.length})
        </button>
        <button
          style={{
            padding: '0.5rem 1rem',
            backgroundColor: activeTab === 'joins' ? '#007bff' : '#f8f9fa',
            color: activeTab === 'joins' ? 'white' : '#495057',
            border: '1px solid #dee2e6',
            borderRadius: '4px',
            cursor: 'pointer'
          }}
          onClick={() => setActiveTab('joins')}
        >
          JOIN 이벤트 ({joinEvents.length})
        </button>
      </div>

      {/* 메시지 탭 */}
      {activeTab === 'messages' && (
        <div className="card">
          <div className="card-header">최근 메시지</div>
          <div className="card-body">
            {messages.length === 0 ? (
              <p>메시지가 없습니다.</p>
            ) : (
              <table className="table">
                <thead>
                  <tr>
                    <th>시간</th>
                    <th>메시지</th>
                    <th>RSSI</th>
                    <th>SNR</th>
                    <th>신호 품질</th>
                    <th>프레임 수</th>
                  </tr>
                </thead>
                <tbody>
                  {messages.map(message => (
                    <tr key={message.id}>
                      <td>{formatTimestamp(message.timestamp)}</td>
                      <td>
                        {message.payloadText || (
                          <span style={{ color: '#6c757d', fontStyle: 'italic' }}>
                            [바이너리 데이터]
                          </span>
                        )}
                      </td>
                      <td>{message.rssi} dBm</td>
                      <td>{message.snr} dB</td>
                      <td>{formatSignalQuality(message.signalQuality)}</td>
                      <td>{message.frameCount}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            )}
          </div>
        </div>
      )}

      {/* JOIN 이벤트 탭 */}
      {activeTab === 'joins' && (
        <div className="card">
          <div className="card-header">JOIN 이벤트</div>
          <div className="card-body">
            {joinEvents.length === 0 ? (
              <p>JOIN 이벤트가 없습니다.</p>
            ) : (
              <table className="table">
                <thead>
                  <tr>
                    <th>시간</th>
                    <th>DevEUI</th>
                    <th>JoinEUI</th>
                    <th>DevAddr</th>
                    <th>RSSI</th>
                    <th>SNR</th>
                    <th>신호 품질</th>
                  </tr>
                </thead>
                <tbody>
                  {joinEvents.map(event => (
                    <tr key={event.id}>
                      <td>{formatTimestamp(event.timestamp)}</td>
                      <td><code style={{ fontSize: '0.85em' }}>{event.devEui}</code></td>
                      <td><code style={{ fontSize: '0.85em' }}>{event.joinEui}</code></td>
                      <td><code style={{ fontSize: '0.85em' }}>{event.devAddr}</code></td>
                      <td>{event.rssi} dBm</td>
                      <td>{event.snr} dB</td>
                      <td>{formatSignalQuality(event.signalQuality)}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            )}
          </div>
        </div>
      )}
    </div>
  );
};

export default DeviceView;