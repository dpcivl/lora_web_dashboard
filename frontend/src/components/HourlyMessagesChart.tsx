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
  hourlyCounts: HourlyCount[];
}

const HourlyMessagesChart: React.FC<HourlyMessagesChartProps> = ({ hourlyCounts }) => {
  const data = {
    labels: hourlyCounts.map(item => {
      const date = new Date(item.hour);
      return date.toLocaleTimeString('ko-KR', { 
        hour: '2-digit', 
        minute: '2-digit' 
      });
    }),
    datasets: [
      {
        label: '시간별 메시지 수',
        data: hourlyCounts.map(item => item.count),
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
        <Line data={data} options={options} />
      </div>
    </div>
  );
};

export default HourlyMessagesChart;