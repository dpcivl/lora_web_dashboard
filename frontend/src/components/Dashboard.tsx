import React, { useState, useEffect } from 'react';
import { messageAPI } from '../api/client';
import { Statistics } from '../types';
import StatisticsCards from './StatisticsCards';
import SignalQualityChart from './SignalQualityChart';
import HourlyMessagesChart from './HourlyMessagesChart';
import DeviceCountsTable from './DeviceCountsTable';
import RealtimeMessages from './RealtimeMessages';

const Dashboard: React.FC = () => {
  const [statistics, setStatistics] = useState<Statistics | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    fetchStatistics();
    const interval = setInterval(fetchStatistics, 30000); // 30초마다 업데이트
    return () => clearInterval(interval);
  }, []);

  const fetchStatistics = async () => {
    try {
      setLoading(true);
      const response = await messageAPI.getStatistics();
      setStatistics(response.data);
      setError(null);
    } catch (error) {
      console.error('통계 조회 실패:', error);
      setError('통계 데이터를 불러올 수 없습니다.');
    } finally {
      setLoading(false);
    }
  };

  if (loading && !statistics) {
    return <div className="loading">데이터를 불러오는 중...</div>;
  }

  if (error) {
    return <div className="error">{error}</div>;
  }

  if (!statistics) {
    return <div className="error">통계 데이터가 없습니다.</div>;
  }

  return (
    <div className="dashboard">
      <h2>대시보드</h2>
      
      <StatisticsCards statistics={statistics} />
      
      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem', marginBottom: '1rem' }}>
        <SignalQualityChart signalQuality={statistics.signalQuality} />
        <HourlyMessagesChart hourlyCounts={statistics.hourlyCounts} />
      </div>
      
      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem' }}>
        <DeviceCountsTable deviceCounts={statistics.deviceCounts} />
        <RealtimeMessages />
      </div>
    </div>
  );
};

export default Dashboard;