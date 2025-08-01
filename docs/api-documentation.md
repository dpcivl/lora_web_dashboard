# API 문서

## 기본 정보

- **Base URL**: `http://localhost:8081`
- **Content-Type**: `application/json`
- **CORS**: 모든 오리진 허용 (개발 환경)

## 헬스체크 API

### GET /health
서비스 상태를 확인합니다.

**응답 예시:**
```json
{
  "status": "UP",
  "timestamp": "2024-01-01T10:00:00",
  "service": "LoRa Web Dashboard API",
  "version": "1.0.0"
}
```

### GET /health/info
서비스 상세 정보를 제공합니다.

**응답 예시:**
```json
{
  "application": "LoRa Web Dashboard",
  "description": "Web dashboard for LoRa Gateway Logger data visualization",
  "version": "1.0.0",
  "port": "8081",
  "database": "SQLite",
  "build-time": "2024-01-01T10:00:00"
}
```

## 메시지 관리 API

### GET /messages/recent
최근 메시지 목록을 페이징으로 조회합니다.

**Query Parameters:**
- `page` (optional): 페이지 번호 (기본값: 0)
- `size` (optional): 페이지 크기 (기본값: 20)

**응답 예시:**
```json
{
  "content": [
    {
      "id": 1,
      "timestamp": "2024-01-01T10:00:00",
      "applicationId": "1",
      "deviceId": "device-001",
      "devEui": "0123456789ABCDEF",
      "payloadBase64": "SGVsbG8gV29ybGQ=",
      "payloadHex": "48656C6C6F20576F726C64",
      "payloadText": "Hello World",
      "payloadSize": 11,
      "frameCount": 42,
      "fPort": 1,
      "frequency": 868100000,
      "dataRate": 5,
      "rssi": -85.5,
      "snr": 7.2,
      "latitude": 37.7749,
      "longitude": -122.4194,
      "hostname": "gateway-001",
      "rawTopic": "application/1/device/device-001/event/up",
      "createdAt": "2024-01-01T10:00:00",
      "signalQuality": "GOOD"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 20
  },
  "totalElements": 150,
  "totalPages": 8,
  "first": true,
  "last": false
}
```

### GET /messages/device/{deviceId}
특정 디바이스의 메시지를 조회합니다.

**Path Parameters:**
- `deviceId`: 디바이스 ID

**Query Parameters:**
- `page` (optional): 페이지 번호 (기본값: 0)
- `size` (optional): 페이지 크기 (기본값: 20)

**응답:** `/messages/recent`와 동일한 구조

### GET /messages/device/{deviceId}/latest
특정 디바이스의 최신 메시지를 조회합니다.

**Path Parameters:**
- `deviceId`: 디바이스 ID

**응답 예시:**
```json
{
  "id": 150,
  "timestamp": "2024-01-01T10:00:00",
  "applicationId": "1",
  "deviceId": "device-001",
  "devEui": "0123456789ABCDEF",
  "frameCount": 150,
  "rssi": -82.1,
  "snr": 8.5,
  "signalQuality": "GOOD"
}
```

### GET /messages/application/{applicationId}
특정 애플리케이션의 메시지를 조회합니다.

**Path Parameters:**
- `applicationId`: 애플리케이션 ID

**Query Parameters:**
- `page` (optional): 페이지 번호 (기본값: 0)
- `size` (optional): 페이지 크기 (기본값: 20)

### GET /messages/range
시간 범위 내의 메시지를 조회합니다.

**Query Parameters:**
- `startTime`: 시작 시간 (ISO 8601 형식)
- `endTime`: 종료 시간 (ISO 8601 형식)

**예시:** `/messages/range?startTime=2024-01-01T00:00:00&endTime=2024-01-01T23:59:59`

### GET /messages/statistics
전체 메시지 통계를 조회합니다.

**응답 예시:**
```json
{
  "totalMessages": 5420,
  "totalDevices": 25,
  "messagesLast24Hours": 342,
  "messagesLast7Days": 2150,
  "mostActiveDevice": "device-001",
  "averageSignalQuality": "GOOD",
  "deviceCounts": [
    {
      "deviceId": "device-001",
      "count": 450
    },
    {
      "deviceId": "device-002", 
      "count": 380
    }
  ],
  "hourlyStats": [
    {
      "hour": 0,
      "messageCount": 15
    },
    {
      "hour": 1,
      "messageCount": 12
    }
  ],
  "signalQualityStats": {
    "excellent": 1250,
    "good": 2100,
    "fair": 1580,
    "poor": 490
  }
}
```

## JOIN 이벤트 API

### GET /join-events/recent
최근 JOIN 이벤트를 조회합니다.

**Query Parameters:**
- `page` (optional): 페이지 번호 (기본값: 0)
- `size` (optional): 페이지 크기 (기본값: 20)

**응답 예시:**
```json
{
  "content": [
    {
      "id": 1,
      "timestamp": "2024-01-01T10:00:00",
      "applicationId": "1",
      "deviceId": "device-001",
      "devEui": "0123456789ABCDEF",
      "joinEui": "FEDCBA9876543210",
      "devAddr": "12345678",
      "rssi": -75.0,
      "snr": 9.5,
      "frequency": 868100000,
      "dataRate": 5,
      "hostname": "gateway-001",
      "rawTopic": "application/1/device/device-001/event/join",
      "createdAt": "2024-01-01T10:00:00",
      "signalQuality": "EXCELLENT"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 20
  },
  "totalElements": 45,
  "totalPages": 3
}
```

### GET /join-events/device/{deviceId}  
특정 디바이스의 JOIN 이벤트를 조회합니다.

**Path Parameters:**
- `deviceId`: 디바이스 ID

**Query Parameters:**
- `page` (optional): 페이지 번호 (기본값: 0)
- `size` (optional): 페이지 크기 (기본값: 20)

## WebSocket API

### 연결 엔드포인트
```
ws://localhost:8081/ws
```

### 실시간 메시지 구독
WebSocket 연결 후 다음 형식의 메시지를 수신합니다:

**새로운 업링크 메시지:**
```json
{
  "type": "NEW_MESSAGE",
  "data": {
    "id": 151,
    "timestamp": "2024-01-01T10:01:00",
    "deviceId": "device-001",
    "payloadText": "Sensor data",
    "rssi": -78.2,
    "snr": 6.8,
    "signalQuality": "GOOD"
  }
}
```

**새로운 JOIN 이벤트:**
```json
{
  "type": "NEW_JOIN_EVENT", 
  "data": {
    "id": 46,
    "timestamp": "2024-01-01T10:01:00",
    "deviceId": "device-002",
    "devEui": "ABCDEF0123456789",
    "signalQuality": "EXCELLENT"
  }
}
```

**통계 업데이트:**
```json
{
  "type": "STATISTICS_UPDATE",
  "data": {
    "totalMessages": 5421,
    "messagesLast24Hours": 343,
    "totalDevices": 25
  }
}
```

## 에러 응답

### 일반적인 에러 형식
```json
{
  "timestamp": "2024-01-01T10:00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Device not found: device-999",
  "path": "/messages/device/device-999/latest"
}
```

### HTTP 상태 코드
- `200 OK`: 성공
- `400 Bad Request`: 잘못된 요청 파라미터
- `404 Not Found`: 리소스를 찾을 수 없음
- `500 Internal Server Error`: 서버 내부 오류

## 데이터 타입

### SignalQuality 열거형
- `EXCELLENT`: RSSI > -70 dBm, SNR > 10 dB
- `GOOD`: RSSI > -85 dBm, SNR > 5 dB  
- `FAIR`: RSSI > -100 dBm, SNR > 0 dB
- `POOR`: 그 외의 경우

### 페이징 응답 구조
모든 페이징 API는 Spring Data의 `Page` 객체를 반환하며 다음 필드를 포함합니다:
- `content`: 실제 데이터 배열
- `pageable`: 페이징 정보
- `totalElements`: 전체 요소 수
- `totalPages`: 전체 페이지 수
- `first`: 첫 번째 페이지 여부
- `last`: 마지막 페이지 여부