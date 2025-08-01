import React, { useState, useEffect } from 'react';
import { messageAPI, joinEventAPI } from '../api/client';

interface ApplicationListProps {
  onApplicationSelect: (applicationId: string) => void;
  title: string;
  type?: 'message' | 'join-event';
}

const ApplicationList: React.FC<ApplicationListProps> = ({ onApplicationSelect, title, type = 'message' }) => {
  const [applications, setApplications] = useState<string[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    fetchApplications();
  }, []);

  const fetchApplications = async () => {
    try {
      setLoading(true);
      const response = type === 'join-event' 
        ? await joinEventAPI.getJoinEventApplications()
        : await messageAPI.getApplications();
      setApplications(response.data);
      setError(null);
    } catch (error) {
      console.error('애플리케이션 목록 조회 실패:', error);
      setError('애플리케이션 목록을 불러올 수 없습니다.');
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return <div className="loading">애플리케이션 목록을 불러오는 중...</div>;
  }

  if (error) {
    return <div className="error">{error}</div>;
  }

  return (
    <div className="application-list">
      <div className="card">
        <div className="card-header">
          <h2>{title}</h2>
        </div>
        <div className="card-body">
          {applications.length === 0 ? (
            <div style={{ textAlign: 'center', padding: '2rem', color: '#6c757d' }}>
              애플리케이션이 없습니다.
            </div>
          ) : (
            <div className="application-grid">
              {applications.map(applicationId => (
                <div 
                  key={applicationId} 
                  className="application-card"
                  onClick={() => onApplicationSelect(applicationId)}
                  style={{
                    border: '1px solid #ddd',
                    borderRadius: '8px',
                    padding: '1.5rem',
                    margin: '0.5rem',
                    cursor: 'pointer',
                    textAlign: 'center',
                    backgroundColor: '#f8f9fa',
                    transition: 'all 0.2s ease',
                  }}
                  onMouseEnter={(e) => {
                    e.currentTarget.style.backgroundColor = '#e9ecef';
                    e.currentTarget.style.transform = 'translateY(-2px)';
                    e.currentTarget.style.boxShadow = '0 4px 8px rgba(0,0,0,0.1)';
                  }}
                  onMouseLeave={(e) => {
                    e.currentTarget.style.backgroundColor = '#f8f9fa';
                    e.currentTarget.style.transform = 'translateY(0)';
                    e.currentTarget.style.boxShadow = 'none';
                  }}
                >
                  <h3 style={{ margin: '0.5rem 0', color: '#007bff' }}>
                    애플리케이션 {applicationId}
                  </h3>
                  <p style={{ margin: '0', color: '#6c757d', fontSize: '0.9rem' }}>
                    클릭하여 데이터 보기
                  </p>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default ApplicationList;