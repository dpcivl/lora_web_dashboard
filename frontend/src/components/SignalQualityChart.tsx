import React from 'react';
import { Chart as ChartJS, ArcElement, Tooltip, Legend } from 'chart.js';
import { Doughnut } from 'react-chartjs-2';
import { SignalQualityStats } from '../types';

ChartJS.register(ArcElement, Tooltip, Legend);

interface SignalQualityChartProps {
  signalQuality: SignalQualityStats | null;
}

const SignalQualityChart: React.FC<SignalQualityChartProps> = ({ signalQuality }) => {
  // signalQuality가 null인 경우 기본값 사용
  const defaultSignalQuality = {
    excellent: 0,
    good: 0,
    fair: 0,
    poor: 0
  };
  
  const qualityData = signalQuality || defaultSignalQuality;
  
  const data = {
    labels: ['매우 좋음', '좋음', '보통', '나쁨'],
    datasets: [
      {
        data: [
          qualityData.excellent,
          qualityData.good,
          qualityData.fair,
          qualityData.poor
        ],
        backgroundColor: [
          '#28a745',
          '#17a2b8',
          '#ffc107',
          '#dc3545'
        ],
        borderWidth: 2,
        borderColor: '#fff'
      }
    ]
  };

  const options = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: {
        position: 'bottom' as const,
      },
      tooltip: {
        callbacks: {
          label: function(context: any) {
            const total = context.dataset.data.reduce((a: number, b: number) => a + b, 0);
            const percentage = total > 0 ? ((context.parsed * 100) / total).toFixed(1) : '0.0';
            return `${context.label}: ${context.parsed} (${percentage}%)`;
          }
        }
      }
    }
  };

  const hasData = signalQuality && (signalQuality.excellent + signalQuality.good + signalQuality.fair + signalQuality.poor) > 0;

  return (
    <div className="chart-container">
      <h3>신호 품질 분포</h3>
      <div style={{ height: '300px' }}>
        {hasData ? (
          <Doughnut data={data} options={options} />
        ) : (
          <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', height: '100%', color: '#666' }}>
            신호 품질 데이터가 없습니다
          </div>
        )}
      </div>
    </div>
  );
};

export default SignalQualityChart;