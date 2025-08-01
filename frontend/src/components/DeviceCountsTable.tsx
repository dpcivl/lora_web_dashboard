import React from 'react';
import { Link } from 'react-router-dom';
import { DeviceCount } from '../types';

interface DeviceCountsTableProps {
  deviceCounts: DeviceCount[] | undefined;
}

const DeviceCountsTable: React.FC<DeviceCountsTableProps> = ({ deviceCounts }) => {
  const displayCounts = deviceCounts?.slice(0, 10) || []; // 상위 10개만 표시

  return (
    <div className="card">
      <div className="card-header">
        디바이스별 메시지 수 (최근 7일)
      </div>
      <div className="card-body">
        {displayCounts.length === 0 ? (
          <p>데이터가 없습니다.</p>
        ) : (
          <table className="table">
            <thead>
              <tr>
                <th>순위</th>
                <th>디바이스 ID</th>
                <th>메시지 수</th>
                <th>상세보기</th>
              </tr>
            </thead>
            <tbody>
              {displayCounts.map((device, index) => (
                <tr key={device.deviceId}>
                  <td>{index + 1}</td>
                  <td>
                    <code>{device.deviceId}</code>
                  </td>
                  <td>
                    <strong>{device.count?.toLocaleString() || 0}</strong>
                  </td>
                  <td>
                    <Link 
                      to={`/devices/${device.deviceId}`}
                      style={{ 
                        color: '#007bff', 
                        textDecoration: 'none' 
                      }}
                    >
                      상세보기 →
                    </Link>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </div>
  );
};

export default DeviceCountsTable;