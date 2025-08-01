export interface UplinkMessage {
  id: number;
  timestamp: string;
  applicationId: string;
  deviceId: string;
  devEui: string;
  payloadBase64: string;
  payloadHex: string;
  payloadText: string;
  payloadSize: number;
  frameCount: number;
  fPort?: number;
  frequency?: number;
  dataRate?: number;
  rssi?: number;
  snr?: number;
  latitude?: number;
  longitude?: number;
  hostname: string;
  rawTopic: string;
  createdAt: string;
  signalQuality: SignalQuality;
}

export interface JoinEvent {
  id: number;
  timestamp: string;
  applicationId: string;
  deviceId: string;
  devEui: string;
  joinEui?: string;
  devAddr?: string;
  frequency?: number;
  dataRate?: number;
  rssi?: number;
  snr?: number;
  latitude?: number;
  longitude?: number;
  hostname: string;
  rawTopic: string;
  createdAt: string;
  signalQuality: SignalQuality;
}

export enum SignalQuality {
  EXCELLENT = 'EXCELLENT',
  GOOD = 'GOOD',
  FAIR = 'FAIR',
  POOR = 'POOR'
}

export interface Statistics {
  totalMessages?: number;
  last24HourMessages?: number;
  activeDevices?: number;
  totalJoinEvents?: number;
  recentJoinEvents?: number;
  deviceCounts?: DeviceCount[];
  signalQuality?: SignalQualityStats;
  hourlyCounts?: HourlyCount[];
}

export interface DeviceCount {
  deviceId: string;
  count: number;
}

export interface SignalQualityStats {
  excellent: number;
  good: number;
  fair: number;
  poor: number;
}

export interface HourlyCount {
  hour: string;
  count: number;
}

export interface ApiResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
}