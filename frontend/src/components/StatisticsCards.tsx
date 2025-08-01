import React from 'react';
import { Statistics } from '../types';

interface StatisticsCardsProps {
  statistics: Statistics;
}

const StatisticsCards: React.FC<StatisticsCardsProps> = ({ statistics }) => {
  return (
    <div className="stats-grid">
      <div className="stat-card primary">
        <h3>{statistics.totalMessages.toLocaleString()}</h3>
        <p>총 메시지 수</p>
      </div>
      
      <div className="stat-card success">
        <h3>{statistics.last24HourMessages.toLocaleString()}</h3>
        <p>24시간 메시지</p>
      </div>
      
      <div className="stat-card info">
        <h3>{statistics.activeDevices.toLocaleString()}</h3>
        <p>활성 디바이스</p>
      </div>
      
      <div className="stat-card warning">
        <h3>{statistics.recentJoinEvents.toLocaleString()}</h3>
        <p>최근 JOIN 이벤트</p>
      </div>
    </div>
  );
};

export default StatisticsCards;