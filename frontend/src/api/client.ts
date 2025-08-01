import axios from 'axios';
import { UplinkMessage, JoinEvent, Statistics, ApiResponse } from '../types';

const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8081/api';

const apiClient = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  }
});

// 요청 인터셉터
apiClient.interceptors.request.use(
  config => {
    console.log(`API 요청: ${config.method?.toUpperCase()} ${config.url}`);
    return config;
  },
  error => {
    return Promise.reject(error);
  }
);

// 응답 인터셉터
apiClient.interceptors.response.use(
  response => {
    return response;
  },
  error => {
    console.error('API 오류:', error.response?.status, error.message);
    return Promise.reject(error);
  }
);

export const messageAPI = {
  getRecentMessages: (page = 0, size = 20) => 
    apiClient.get<ApiResponse<UplinkMessage>>(`/messages/recent?page=${page}&size=${size}`),
  
  getDeviceMessages: (deviceId: string, page = 0, size = 20) => 
    apiClient.get<ApiResponse<UplinkMessage>>(`/messages/device/${deviceId}?page=${page}&size=${size}`),
  
  getApplicationMessages: (applicationId: string, page = 0, size = 20) => 
    apiClient.get<ApiResponse<UplinkMessage>>(`/messages/application/${applicationId}?page=${page}&size=${size}`),
  
  getMessagesInRange: (startTime: string, endTime: string) => 
    apiClient.get<UplinkMessage[]>(`/messages/range?startTime=${startTime}&endTime=${endTime}`),
  
  getLatestDeviceMessage: (deviceId: string) => 
    apiClient.get<UplinkMessage>(`/messages/device/${deviceId}/latest`),
  
  getStatistics: () => 
    apiClient.get<Statistics>('/messages/statistics'),
  
  getApplications: () => 
    apiClient.get<string[]>('/messages/applications'),
};

export const joinEventAPI = {
  getRecentJoinEvents: (page = 0, size = 20) => 
    apiClient.get<ApiResponse<JoinEvent>>(`/join-events/recent?page=${page}&size=${size}`),
  
  getDeviceJoinEvents: (deviceId: string, page = 0, size = 20) => 
    apiClient.get<ApiResponse<JoinEvent>>(`/join-events/device/${deviceId}?page=${page}&size=${size}`),
  
  getLatestDeviceJoinEvent: (deviceId: string) => 
    apiClient.get<JoinEvent>(`/join-events/device/${deviceId}/latest`),
  
  getJoinEventApplications: () => 
    apiClient.get<string[]>('/join-events/applications'),
};

export const healthAPI = {
  getHealth: () => 
    apiClient.get('/health'),
  
  getInfo: () => 
    apiClient.get('/health/info'),
};

export default apiClient;