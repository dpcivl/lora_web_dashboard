# 프로젝트 구조

## 전체 아키텍처

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   React Frontend │    │  Spring Boot    │    │   SQLite DB     │
│   (Port 3000)   │◄──►│   Backend       │◄──►│  lora_gateway   │
│                 │    │   (Port 8081)   │    │                 │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         ▲                       ▲
         │                       │
         ▼                       ▼
┌─────────────────┐    ┌─────────────────┐
│   WebSocket     │    │   MQTT Broker   │
│   Connection    │    │   (Port 1883)   │
└─────────────────┘    └─────────────────┘
```

## 디렉터리 구조

### 루트 디렉터리
```
lora_web_dashboard/
├── docs/                          # 📚 프로젝트 문서
├── src/                           # ☕ 백엔드 Java 소스코드
├── frontend/                      # ⚛️ 프론트엔드 React 소스코드
├── docker-compose.yml             # 🐳 Docker 서비스 구성
├── docker-compose-minimal.yml     # 🐳 최소 Docker 구성
├── Dockerfile                     # 🐳 멀티스테이지 빌드
├── pom.xml                        # 📦 Maven 프로젝트 설정
├── .gitignore                     # 🚫 Git 무시 파일
├── nginx.conf                     # 🌐 Nginx 설정
├── mosquitto.conf                 # 📡 MQTT 브로커 설정
└── start.sh                       # 🚀 시작 스크립트
```

## 백엔드 구조 (Spring Boot)

### 패키지 구조
```
src/main/java/com/lora/dashboard/
├── LoraWebDashboardApplication.java   # 🚀 메인 애플리케이션
├── config/                            # ⚙️ 설정 클래스
│   ├── AsyncConfig.java              # 비동기 처리 설정
│   ├── WebConfig.java                # CORS 및 웹 설정
│   └── WebSocketConfig.java          # WebSocket 설정
├── controller/                        # 🎮 REST API 컨트롤러
│   ├── HealthController.java         # 헬스체크 API
│   ├── MessageController.java        # 메시지 관리 API
│   └── JoinEventController.java      # JOIN 이벤트 API
├── service/                           # 💼 비즈니스 로직
│   ├── MessageService.java           # 메시지 서비스
│   └── RealtimeService.java          # 실시간 데이터 서비스
├── repository/                        # 🗄️ 데이터 액세스 계층
│   ├── UplinkMessageRepository.java  # 업링크 메시지 리포지토리
│   └── JoinEventRepository.java      # JOIN 이벤트 리포지토리
├── entity/                            # 📋 JPA 엔티티
│   ├── UplinkMessage.java            # 업링크 메시지 엔티티
│   ├── JoinEvent.java                # JOIN 이벤트 엔티티
│   └── SignalQuality.java            # 신호 품질 열거형
├── dto/                               # 📤 데이터 전송 객체
│   └── StatisticsDto.java            # 통계 DTO
└── websocket/                         # 🔌 WebSocket 핸들러
    └── MessageWebSocketHandler.java  # 메시지 WebSocket 핸들러
```

### 주요 클래스 설명

#### 컨트롤러 (Controller)
- **HealthController**: 서비스 상태 확인 및 정보 제공
- **MessageController**: 업링크 메시지 CRUD 및 통계
- **JoinEventController**: JOIN 이벤트 관리

#### 서비스 (Service)
- **MessageService**: 메시지 처리, 통계 계산, 데이터 집계
- **RealtimeService**: WebSocket을 통한 실시간 데이터 푸시

#### 엔티티 (Entity)
- **UplinkMessage**: LoRa 업링크 메시지 데이터 모델
- **JoinEvent**: LoRa JOIN 이벤트 데이터 모델
- **SignalQuality**: 신호 품질 등급 (EXCELLENT, GOOD, FAIR, POOR)

## 프론트엔드 구조 (React)

### 디렉터리 구조
```
frontend/
├── public/                        # 🌐 정적 파일
│   └── index.html                # HTML 엔트리포인트
├── src/                          # ⚛️ React 소스코드
│   ├── components/               # 🧩 React 컴포넌트
│   │   ├── Dashboard.tsx         # 메인 대시보드
│   │   ├── Header.tsx            # 헤더 컴포넌트
│   │   ├── Sidebar.tsx           # 사이드바 네비게이션
│   │   ├── DeviceView.tsx        # 디바이스 상세보기
│   │   ├── DeviceCountsTable.tsx # 디바이스 목록 테이블
│   │   ├── MessageList.tsx       # 메시지 목록
│   │   ├── JoinEventList.tsx     # JOIN 이벤트 목록
│   │   ├── StatisticsCards.tsx   # 통계 카드
│   │   ├── HourlyMessagesChart.tsx # 시간별 차트
│   │   ├── SignalQualityChart.tsx  # 신호품질 차트
│   │   └── RealtimeMessages.tsx  # 실시간 메시지
│   ├── hooks/                    # 🎣 커스텀 훅
│   │   └── useWebSocket.ts       # WebSocket 훅
│   ├── api/                      # 🌐 API 클라이언트
│   │   └── client.ts             # HTTP 클라이언트
│   ├── types/                    # 📝 TypeScript 타입
│   │   └── index.ts              # 타입 정의
│   ├── App.tsx                   # 📱 메인 앱 컴포넌트
│   ├── index.tsx                 # 🚀 앱 엔트리포인트
│   ├── App.css                   # 🎨 앱 스타일
│   └── index.css                 # 🎨 글로벌 스타일
├── package.json                  # 📦 NPM 의존성
└── tsconfig.json                # ⚙️ TypeScript 설정
```

### 주요 컴포넌트 설명

#### 페이지 컴포넌트
- **Dashboard**: 메인 대시보드 (통계, 차트, 디바이스 목록)
- **DeviceView**: 디바이스 상세 정보 페이지

#### UI 컴포넌트
- **Header**: 상단 헤더 (제목, 네비게이션)
- **Sidebar**: 좌측 사이드바 (메뉴)
- **StatisticsCards**: 통계 요약 카드들
- **DeviceCountsTable**: 디바이스별 메시지 수 테이블

#### 차트 컴포넌트
- **HourlyMessagesChart**: 시간별 메시지 수 차트
- **SignalQualityChart**: 신호 품질 분포 차트

#### 데이터 컴포넌트
- **MessageList**: 메시지 목록 테이블
- **JoinEventList**: JOIN 이벤트 목록
- **RealtimeMessages**: 실시간 메시지 스트림

## 설정 파일

### 백엔드 설정
```
src/main/resources/
└── application.yml               # Spring Boot 설정
```

### Docker 설정
- **Dockerfile**: 멀티스테이지 빌드 (프론트엔드 빌드 → 백엔드 빌드 → 최종 이미지)
- **docker-compose.yml**: 전체 서비스 구성 (웹앱, MQTT, LoRa Logger)
- **docker-compose-minimal.yml**: 웹앱과 MQTT만 포함

### 웹서버 설정
- **nginx.conf**: Nginx 설정 (정적 파일 서빙, API 프록시)

## 데이터 흐름

### 1. 데이터 수집 흐름
```
LoRa Gateway → MQTT Broker → LoRa Gateway Logger → SQLite DB
```

### 2. 웹 대시보드 데이터 흐름
```
SQLite DB → Spring Boot API → React Frontend → 사용자
```

### 3. 실시간 데이터 흐름
```
SQLite DB → RealtimeService → WebSocket → React Hook → UI 업데이트
```

## 빌드 및 배포

### 개발 환경
- 백엔드: `mvn spring-boot:run` (포트 8081)
- 프론트엔드: `npm start` (포트 3000)

### 프로덕션 환경
- Docker: `docker-compose up -d`
- 웹 접속: http://localhost:3001
- API 접속: http://localhost:8081

## 의존성

### 백엔드 주요 의존성
- Spring Boot Web
- Spring Boot Data JPA
- Spring Boot WebSocket
- SQLite JDBC Driver
- Spring Boot DevTools

### 프론트엔드 주요 의존성
- React 18
- TypeScript
- React Router DOM
- Chart.js
- Axios (HTTP 클라이언트)