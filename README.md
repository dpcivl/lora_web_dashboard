# LoRa Web Dashboard

## 개요

LoRa Gateway Logger에서 수집된 데이터를 시각화하고 모니터링하는 웹 대시보드입니다. 
실시간으로 LoRa 디바이스의 메시지를 확인하고, 통계 정보를 제공하며, 디바이스별 상세 정보를 볼 수 있습니다.

## 주요 기능

### 📊 대시보드
- 실시간 메시지 수신 현황
- 시간대별 메시지 통계 차트
- 신호 품질 분석 차트
- 디바이스별 메시지 수 순위

### 🔍 디바이스 관리
- 디바이스 목록 및 메시지 수 표시
- 디바이스별 상세 정보 페이지
- Frame Count 추적
- 신호 품질 모니터링 (RSSI, SNR)
- JOIN 이벤트 추적

### 📈 실시간 모니터링
- WebSocket을 통한 실시간 데이터 업데이트
- 메시지 스트리밍
- 자동 통계 갱신

## 기술 스택

### 백엔드
- **Spring Boot 3.x** - 메인 프레임워크
- **Spring Data JPA** - 데이터베이스 ORM
- **SQLite** - 데이터베이스
- **WebSocket** - 실시간 통신
- **Java 17** - 프로그래밍 언어

### 프론트엔드
- **React 18** - UI 프레임워크
- **TypeScript** - 타입 안전성
- **Chart.js** - 데이터 시각화
- **WebSocket Client** - 실시간 통신

### 인프라
- **Docker & Docker Compose** - 컨테이너화
- **Nginx** - 웹서버 (프로덕션)
- **MQTT Broker (Mosquitto)** - 메시지 브로커

## 프로젝트 구조

```
lora_web_dashboard/
├── docs/                          # 문서
├── src/main/java/                 # 백엔드 소스코드
│   └── com/lora/dashboard/
│       ├── controller/            # REST API 컨트롤러
│       ├── service/               # 비즈니스 로직
│       ├── repository/            # 데이터 액세스
│       ├── entity/                # JPA 엔티티
│       ├── dto/                   # 데이터 전송 객체
│       ├── config/                # 설정 클래스
│       └── websocket/             # WebSocket 핸들러
├── frontend/                      # 프론트엔드 소스코드
│   ├── src/
│   │   ├── components/            # React 컴포넌트
│   │   ├── hooks/                 # 커스텀 훅
│   │   ├── api/                   # API 클라이언트
│   │   └── types/                 # TypeScript 타입
│   └── public/                    # 정적 파일
├── docker-compose.yml             # Docker 구성
├── Dockerfile                     # Docker 이미지 빌드
├── pom.xml                        # Maven 설정
└── README.md                      # 프로젝트 개요
```

## 빠른 시작

### 1. 사전 요구사항
- Java 17+
- Node.js 18+
- Maven 3.6+
- Docker & Docker Compose (선택사항)

### 2. 의존성 설치
```bash
# 프론트엔드 의존성 설치
cd frontend
npm install

# 루트 디렉터리로 돌아가기
cd ..
```

### 3. 실행 방법

#### Docker 사용 (권장)
```bash
# 모든 서비스 실행
docker-compose up -d

# 접속
# 웹 대시보드: http://localhost:3001
# 백엔드 API: http://localhost:8081
```

#### 개발 모드 실행
```bash
# 백엔드 실행 (터미널 1)
mvn spring-boot:run

# 프론트엔드 실행 (터미널 2)
cd frontend
npm start

# 접속
# 웹 대시보드: http://localhost:3000
# 백엔드 API: http://localhost:8081
```

### 4. 데이터베이스 설정
- SQLite 데이터베이스 파일 `lora_gateway.db`를 프로젝트 루트에 배치
- LoRa Gateway Logger에서 생성된 데이터베이스를 사용

## API 엔드포인트

### 메시지 관리
- `GET /messages/recent` - 최근 메시지 조회
- `GET /messages/device/{deviceId}` - 디바이스별 메시지 조회
- `GET /messages/device/{deviceId}/latest` - 디바이스 최신 메시지
- `GET /messages/statistics` - 통계 정보

### JOIN 이벤트
- `GET /join-events/recent` - 최근 JOIN 이벤트
- `GET /join-events/device/{deviceId}` - 디바이스별 JOIN 이벤트

### 헬스체크
- `GET /health` - 서비스 상태 확인
- `GET /health/info` - 서비스 정보

## 환경 설정

### 백엔드 설정 (application.yml)
```yaml
spring:
  datasource:
    url: jdbc:sqlite:lora_gateway.db
  jpa:
    database-platform: org.hibernate.dialect.SQLiteDialect
    
server:
  port: 8081
```

### 프론트엔드 환경변수
```env
REACT_APP_API_BASE_URL=http://localhost:8081
REACT_APP_WS_URL=ws://localhost:8081/ws
```

## 개발 가이드 링크

- [프로젝트 구조 상세](./docs/project-structure.md)
- [API 문서](./docs/api-documentation.md)
- [데이터베이스 스키마](./docs/database-schema.md)
- [배포 가이드](./docs/deployment-guide.md)
- [개발 환경 설정](./docs/development-setup.md)
- [트러블슈팅](./docs/troubleshooting.md)

## 라이선스

이 프로젝트는 MIT 라이선스 하에 배포됩니다.