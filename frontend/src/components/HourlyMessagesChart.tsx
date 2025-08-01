import React from 'react';
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend,
} from 'chart.js';
import { Line } from 'react-chartjs-2';
import { HourlyCount } from '../types';

ChartJS.register(
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend
);

interface HourlyMessagesChartProps {
  hourlyCounts: HourlyCount[] | undefined;
}

const HourlyMessagesChart: React.FC<HourlyMessagesChartProps> = ({ hourlyCounts }) => {
  // hourlyCounts가 undefined인 경우 빈 배열 사용
  const counts = hourlyCounts || [];
  
  const data = {
    labels: counts.map(item => {
      // "yyyy-MM-dd HH:00" 형식을 파싱
      const dateStr = item.hour.replace(' ', 'T'); // ISO 형식으로 변환
      const date = new Date(dateStr);
      return date.toLocaleTimeString('ko-KR', { 
        month: 'short',
        day: 'numeric',
        hour: '2-digit', 
        minute: '2-digit' 
      });
    }),
    datasets: [
      {
        label: '시간별 메시지 수',
        data: counts.map(item => item.count),
        borderColor: '#007bff',
        backgroundColor: 'rgba(0, 123, 255, 0.1)',
        tension: 0.4,
        fill: true,
      }
    ]
  };

  const options = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: {
        position: 'top' as const,
      },
      title: {
        display: false,
      },
    },
    scales: {
      y: {
        beginAtZero: true,
        ticks: {
          stepSize: 1,
        },
      },
      x: {
        ticks: {
          maxTicksLimit: 12,
        },
      },
    },
  };

  return (
    <div className="chart-container">
      <h3>시간별 메시지 수 (최근 24시간)</h3>
      <div style={{ height: '300px' }}>
        {counts.length > 0 ? (
          <Line data={data} options={options} />
        ) : (
          <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', height: '100%', color: '#666' }}>
            시간별 데이터가 없습니다
          </div>
        )}
      </div>
    </div>
  );
};

export default HourlyMessagesChart;