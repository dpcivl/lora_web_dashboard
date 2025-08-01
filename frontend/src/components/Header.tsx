import React from 'react';

const Header: React.FC = () => {
  return (
    <header className="header">
      <h1>LoRa Gateway Web Dashboard</h1>
      <div className="header-subtitle">
        실시간 LoRa 디바이스 데이터 모니터링 및 분석
      </div>
    </header>
  );
};

export default Header;