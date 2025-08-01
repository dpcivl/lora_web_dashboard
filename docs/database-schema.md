# 데이터베이스 스키마

## 개요

LoRa Web Dashboard는 SQLite 데이터베이스를 사용하며, LoRa Gateway Logger에서 생성된 테이블 구조를 기반으로 합니다.

## 테이블 구조

### uplink_messages

LoRa 디바이스에서 전송된 업링크 메시지를 저장하는 메인 테이블입니다.

```sql
CREATE TABLE uplink_messages (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    timestamp DATETIME NOT NULL,
    application_id VARCHAR(50) NOT NULL,
    device_id VARCHAR(100) NOT NULL,
    dev_eui VARCHAR(16),
    payload_base64 TEXT,
    payload_hex TEXT,
    payload_text TEXT,
    payload_size INTEGER,
    frame_count INTEGER,
    f_port INTEGER,
    frequency INTEGER,
    data_rate INTEGER,
    rssi REAL,
    snr REAL,
    latitude REAL,
    longitude REAL,
    hostname VARCHAR(100),
    raw_topic TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);
```

#### 필드 설명

| 필드명 | 타입 | 설명 | 예시 |
|--------|------|------|------|
| `id` | INTEGER | 기본키 (자동증가) | 1, 2, 3... |
| `timestamp` | DATETIME | 메시지 수신 시간 | 2024-01-01 10:00:00 |
| `application_id` | VARCHAR(50) | LoRaWAN 애플리케이션 ID | "1", "my-app" |
| `device_id` | VARCHAR(100) | 디바이스 식별자 | "device-001", "sensor-01" |
| `dev_eui` | VARCHAR(16) | 디바이스 EUI (64bit 헥스) | "0123456789ABCDEF" |
| `payload_base64` | TEXT | Base64 인코딩된 페이로드 | "SGVsbG8gV29ybGQ=" |
| `payload_hex` | TEXT | 16진수 페이로드 | "48656C6C6F20576F726C64" |
| `payload_text` | TEXT | 텍스트 페이로드 (가능한 경우) | "Hello World" |
| `payload_size` | INTEGER | 페이로드 크기 (바이트) | 11 |
| `frame_count` | INTEGER | LoRaWAN 프레임 카운터 | 42, 150 |
| `f_port` | INTEGER | LoRaWAN FPort | 1, 2, 10 |
| `frequency` | INTEGER | 전송 주파수 (Hz) | 868100000 |
| `data_rate` | INTEGER | LoRaWAN 데이터 레이트 | 0-15 |
| `rssi` | REAL | 수신 신호 강도 (dBm) | -85.5 |
| `snr` | REAL | 신호 대 잡음비 (dB) | 7.2 |
| `latitude` | REAL | 위도 (GPS) | 37.7749 |
| `longitude` | REAL | 경도 (GPS) | -122.4194 |
| `hostname` | VARCHAR(100) | 게이트웨이 호스트명 | "gateway-001" |
| `raw_topic` | TEXT | 원본 MQTT 토픽 | "application/1/device/device-001/event/up" |
| `created_at` | DATETIME | 레코드 생성 시간 | 2024-01-01 10:00:00 |

#### 인덱스

```sql
CREATE INDEX idx_uplink_messages_device_id ON uplink_messages(device_id);
CREATE INDEX idx_uplink_messages_timestamp ON uplink_messages(timestamp);
CREATE INDEX idx_uplink_messages_application_id ON uplink_messages(application_id);
CREATE INDEX idx_uplink_messages_created_at ON uplink_messages(created_at);
```

### join_events

LoRa 디바이스의 JOIN 이벤트를 저장하는 테이블입니다.

```sql
CREATE TABLE join_events (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    timestamp DATETIME NOT NULL,
    application_id VARCHAR(50) NOT NULL,
    device_id VARCHAR(100) NOT NULL,
    dev_eui VARCHAR(16),
    join_eui VARCHAR(16),
    dev_addr VARCHAR(8),
    rssi REAL,
    snr REAL,
    frequency INTEGER,
    data_rate INTEGER,
    hostname VARCHAR(100),
    raw_topic TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);
```

#### 필드 설명

| 필드명 | 타입 | 설명 | 예시 |
|--------|------|------|------|
| `id` | INTEGER | 기본키 (자동증가) | 1, 2, 3... |
| `timestamp` | DATETIME | JOIN 이벤트 발생 시간 | 2024-01-01 10:00:00 |
| `application_id` | VARCHAR(50) | LoRaWAN 애플리케이션 ID | "1" |
| `device_id` | VARCHAR(100) | 디바이스 식별자 | "device-001" |
| `dev_eui` | VARCHAR(16) | 디바이스 EUI | "0123456789ABCDEF" |
| `join_eui` | VARCHAR(16) | JOIN EUI (AppEUI) | "FEDCBA9876543210" |
| `dev_addr` | VARCHAR(8) | 디바이스 주소 (32bit) | "12345678" |
| `rssi` | REAL | 수신 신호 강도 (dBm) | -75.0 |
| `snr` | REAL | 신호 대 잡음비 (dB) | 9.5 |
| `frequency` | INTEGER | 전송 주파수 (Hz) | 868100000 |
| `data_rate` | INTEGER | LoRaWAN 데이터 레이트 | 5 |
| `hostname` | VARCHAR(100) | 게이트웨이 호스트명 | "gateway-001" |
| `raw_topic` | TEXT | 원본 MQTT 토픽 | "application/1/device/device-001/event/join" |
| `created_at` | DATETIME | 레코드 생성 시간 | 2024-01-01 10:00:00 |

#### 인덱스

```sql
CREATE INDEX idx_join_events_device_id ON join_events(device_id);
CREATE INDEX idx_join_events_timestamp ON join_events(timestamp);
CREATE INDEX idx_join_events_application_id ON join_events(application_id);
CREATE INDEX idx_join_events_created_at ON join_events(created_at);
```

## JPA 엔티티 매핑

### UplinkMessage 엔티티

```java
@Entity
@Table(name = "uplink_messages")
public class UplinkMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;
    
    @Column(name = "device_id", nullable = false)
    private String deviceId;
    
    @Column(name = "frame_count")
    private Integer frameCount;
    
    @Column(name = "rssi")
    private Float rssi;
    
    @Column(name = "snr")
    private Float snr;
    
    // 신호 품질 계산 메서드
    public SignalQuality getSignalQuality() {
        if (rssi != null && snr != null) {
            if (rssi > -70 && snr > 10) return SignalQuality.EXCELLENT;
            if (rssi > -85 && snr > 5) return SignalQuality.GOOD;
            if (rssi > -100 && snr > 0) return SignalQuality.FAIR;
        }
        return SignalQuality.POOR;
    }
}
```

### JoinEvent 엔티티

```java
@Entity
@Table(name = "join_events")
public class JoinEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;
    
    @Column(name = "device_id", nullable = false)
    private String deviceId;
    
    @Column(name = "dev_eui")
    private String devEui;
    
    @Column(name = "join_eui")
    private String joinEui;
    
    @Column(name = "dev_addr")
    private String devAddr;
    
    // 신호 품질 계산 메서드
    public SignalQuality getSignalQuality() {
        if (rssi != null && snr != null) {
            if (rssi > -70 && snr > 10) return SignalQuality.EXCELLENT;
            if (rssi > -85 && snr > 5) return SignalQuality.GOOD;
            if (rssi > -100 && snr > 0) return SignalQuality.FAIR;
        }
        return SignalQuality.POOR;
    }
}
```

## 주요 쿼리

### 통계 쿼리

#### 디바이스별 메시지 수 (최근 7일)
```sql
SELECT 
    device_id,
    COUNT(*) as message_count
FROM uplink_messages 
WHERE created_at >= datetime('now', '-7 days')
GROUP BY device_id 
ORDER BY message_count DESC;
```

#### 시간별 메시지 통계 (최근 24시간)
```sql
SELECT 
    strftime('%H', timestamp) as hour,
    COUNT(*) as message_count
FROM uplink_messages 
WHERE timestamp >= datetime('now', '-1 day')
GROUP BY strftime('%H', timestamp)
ORDER BY hour;
```

#### 신호 품질 분포
```sql
SELECT 
    CASE 
        WHEN rssi > -70 AND snr > 10 THEN 'EXCELLENT'
        WHEN rssi > -85 AND snr > 5 THEN 'GOOD'
        WHEN rssi > -100 AND snr > 0 THEN 'FAIR'
        ELSE 'POOR'
    END as signal_quality,
    COUNT(*) as count
FROM uplink_messages 
WHERE rssi IS NOT NULL AND snr IS NOT NULL
GROUP BY signal_quality;
```

#### 특정 디바이스의 최신 메시지
```sql
SELECT * FROM uplink_messages 
WHERE device_id = ? 
ORDER BY timestamp DESC 
LIMIT 1;
```

#### 특정 디바이스의 최신 Frame Count
```sql
SELECT MAX(frame_count) as latest_frame_count
FROM uplink_messages 
WHERE device_id = ? AND frame_count IS NOT NULL;
```

## 데이터베이스 설정

### application.yml 설정
```yaml
spring:
  datasource:
    url: jdbc:sqlite:lora_gateway.db
    driver-class-name: org.sqlite.JDBC
  jpa:
    database-platform: org.hibernate.dialect.SQLiteDialect
    hibernate:
      ddl-auto: validate  # 테이블 구조 검증만 수행
    show-sql: false
    properties:
      hibernate:
        format_sql: true
```

### SQLite 특별 고려사항

1. **자동증가 ID**: SQLite의 `AUTOINCREMENT`는 JPA의 `GenerationType.IDENTITY`와 매핑
2. **날짜/시간**: `DATETIME` 타입을 `LocalDateTime`으로 매핑
3. **Boolean 타입**: SQLite는 Boolean을 지원하지 않으므로 INTEGER(0/1) 사용
4. **외래키 제약**: 기본적으로 비활성화되어 있음

### 데이터베이스 백업 및 유지보수

#### 백업
```bash
# SQLite 데이터베이스 백업
sqlite3 lora_gateway.db ".backup backup_$(date +%Y%m%d_%H%M%S).db"
```

#### 최적화
```sql
-- 인덱스 재구성
REINDEX;

-- 데이터베이스 최적화
VACUUM;

-- 통계 정보 업데이트  
ANALYZE;
```

#### 오래된 데이터 정리
```sql
-- 30일 이전 데이터 삭제
DELETE FROM uplink_messages 
WHERE created_at < datetime('now', '-30 days');

DELETE FROM join_events 
WHERE created_at < datetime('now', '-30 days');
```