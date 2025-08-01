# 개발 환경 설정

## 개발 환경 요구사항

### 필수 소프트웨어

#### 백엔드 개발
- **Java 17+** (OpenJDK 또는 Oracle JDK)
- **Maven 3.6+** (빌드 도구)
- **Git** (버전 관리)

#### 프론트엔드 개발  
- **Node.js 18+** (JavaScript 런타임)
- **npm 8+** (패키지 매니저)

#### 선택사항
- **Docker & Docker Compose** (컨테이너 환경)
- **IDE**: IntelliJ IDEA, Visual Studio Code, Eclipse
- **데이터베이스 도구**: DB Browser for SQLite, DBeaver

## 로컬 개발 환경 구축

### 1. 저장소 클론
```bash
git clone https://github.com/your-repo/lora-web-dashboard.git
cd lora-web-dashboard
```

### 2. 백엔드 설정

#### Java 및 Maven 설치 확인
```bash
# Java 버전 확인
java -version

# Maven 버전 확인
mvn -version
```

#### 의존성 설치 및 빌드
```bash
# 의존성 다운로드 및 컴파일
mvn clean compile

# 테스트 실행
mvn test

# 패키징 (선택사항)
mvn package -DskipTests
```

### 3. 프론트엔드 설정

#### Node.js 설치 확인
```bash
# Node.js 버전 확인
node --version

# npm 버전 확인
npm --version
```

#### 의존성 설치
```bash
cd frontend

# 의존성 설치
npm install

# 개발 서버 실행 (선택사항)
npm start
```

### 4. 데이터베이스 설정

#### 개발용 SQLite 데이터베이스 생성
```bash
# 프로젝트 루트에서
touch lora_gateway.db

# 또는 기존 데이터베이스 복사 (권장)
cp /path/to/existing/lora_gateway.db ./lora_gateway.db
```

#### 테스트 데이터 생성 (선택사항)
```sql
-- SQLite 콘솔에서 실행
sqlite3 lora_gateway.db

-- 테스트 데이터 삽입
INSERT INTO uplink_messages (
    timestamp, application_id, device_id, dev_eui, 
    payload_text, frame_count, rssi, snr, created_at
) VALUES 
('2024-01-01 10:00:00', '1', 'test-device-001', '0123456789ABCDEF', 
 'Hello World', 1, -75.5, 8.2, datetime('now')),
('2024-01-01 10:01:00', '1', 'test-device-002', 'FEDCBA9876543210', 
 'Sensor Data', 1, -82.1, 6.5, datetime('now'));

INSERT INTO join_events (
    timestamp, application_id, device_id, dev_eui, join_eui, 
    dev_addr, rssi, snr, created_at
) VALUES 
('2024-01-01 09:59:00', '1', 'test-device-001', '0123456789ABCDEF', 
 'ABCDEF0123456789', '12345678', -70.0, 9.5, datetime('now'));

.exit
```

## 개발 서버 실행

### 방법 1: 개별 실행 (권장)

#### 터미널 1: 백엔드 실행
```bash
# 프로젝트 루트에서
mvn spring-boot:run

# 또는 JAR 파일로 실행
java -jar target/lora-web-dashboard-1.0.0.jar
```

#### 터미널 2: 프론트엔드 실행
```bash
cd frontend
npm start
```

#### 접속 정보
- **프론트엔드**: http://localhost:3000
- **백엔드 API**: http://localhost:8081
- **헬스체크**: http://localhost:8081/health

### 방법 2: Docker Compose
```bash
# 개발용 Docker Compose 실행
docker-compose -f docker-compose.yml up -d

# 접속 정보
# - 웹 대시보드: http://localhost:3001
# - 백엔드 API: http://localhost:8081
```

## IDE 설정

### Visual Studio Code

#### 1. 확장 프로그램 설치
```json
// .vscode/extensions.json
{
  "recommendations": [
    "vscjava.vscode-java-pack",
    "ms-vscode.vscode-typescript-next",
    "bradlc.vscode-tailwindcss",
    "esbenp.prettier-vscode",
    "ms-vscode.vscode-json"
  ]
}
```

#### 2. 워크스페이스 설정
```json
// .vscode/settings.json
{
  "java.home": "/path/to/java17",
  "java.configuration.runtimes": [
    {
      "name": "JavaSE-17",
      "path": "/path/to/java17"
    }
  ],
  "java.compile.nullAnalysis.mode": "automatic",
  "typescript.preferences.quoteStyle": "single",
  "editor.formatOnSave": true,
  "editor.codeActionsOnSave": {
    "source.organizeImports": true
  }
}
```

#### 3. 실행 구성
```json
// .vscode/launch.json
{
  "version": "0.2.0",
  "configurations": [
    {
      "type": "java",
      "name": "Spring Boot App",
      "request": "launch",
      "mainClass": "com.lora.dashboard.LoraWebDashboardApplication",
      "projectName": "lora-web-dashboard",
      "args": [],
      "envFile": "${workspaceFolder}/.env"
    }
  ]
}
```

### IntelliJ IDEA

#### 1. 프로젝트 열기
- File → Open → 프로젝트 루트 디렉터리 선택
- Maven 프로젝트로 인식되면 자동 설정

#### 2. 실행 구성
- Run → Edit Configurations...
- '+' → Spring Boot
- Main class: `com.lora.dashboard.LoraWebDashboardApplication`
- VM options: `-Dspring.profiles.active=dev`

#### 3. 코드 스타일 설정
- File → Settings → Editor → Code Style
- Java: Google Java Style 또는 Spring Boot 스타일 적용

## 환경 변수 설정

### 개발용 환경 변수 파일 생성
```bash
# .env 파일 생성 (프로젝트 루트)
cat > .env << 'EOF'
# 데이터베이스 설정
SPRING_DATASOURCE_URL=jdbc:sqlite:lora_gateway.db

# 서버 포트
SERVER_PORT=8081

# 로깅 설정
LOGGING_LEVEL_ROOT=INFO
LOGGING_LEVEL_COM_LORA_DASHBOARD=DEBUG

# 개발 모드
SPRING_PROFILES_ACTIVE=dev
EOF
```

### application-dev.yml 생성
```bash
# src/main/resources/application-dev.yml
cat > src/main/resources/application-dev.yml << 'EOF'
spring:
  datasource:
    url: jdbc:sqlite:lora_gateway.db
  jpa:
    show-sql: true
    properties:
      hibernate:
        format_sql: true

logging:
  level:
    com.lora.dashboard: DEBUG
    org.springframework.web: DEBUG
  pattern:
    console: "%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"

server:
  port: 8081
EOF
```

### 프론트엔드 환경 변수
```bash
# frontend/.env.development
cat > frontend/.env.development << 'EOF'
REACT_APP_API_BASE_URL=http://localhost:8081
REACT_APP_WS_URL=ws://localhost:8081/ws
GENERATE_SOURCEMAP=true
EOF
```

## 개발 도구 및 스크립트

### 개발 유틸리티 스크립트
```bash
# scripts/dev.sh 생성
cat > scripts/dev.sh << 'EOF'
#!/bin/bash

case "$1" in
  "start")
    echo "Starting development servers..."
    # 백엔드 시작 (백그라운드)
    mvn spring-boot:run &
    BACKEND_PID=$!
    
    # 프론트엔드 시작 (백그라운드) 
    cd frontend && npm start &
    FRONTEND_PID=$!
    
    echo "Backend PID: $BACKEND_PID"
    echo "Frontend PID: $FRONTEND_PID"
    echo "Press Ctrl+C to stop all servers"
    
    # 종료 신호 처리
    trap "kill $BACKEND_PID $FRONTEND_PID; exit" INT
    wait
    ;;
    
  "build")
    echo "Building application..."
    mvn clean package -DskipTests
    cd frontend && npm run build
    ;;
    
  "test")
    echo "Running tests..."
    mvn test
    cd frontend && npm test -- --watchAll=false
    ;;
    
  "clean")
    echo "Cleaning build artifacts..."
    mvn clean
    cd frontend && rm -rf build node_modules package-lock.json
    ;;
    
  *)
    echo "Usage: $0 {start|build|test|clean}"
    exit 1
    ;;
esac
EOF

chmod +x scripts/dev.sh
```

### 코드 품질 도구 설정

#### ESLint 설정 (프론트엔드)
```bash
cd frontend

# ESLint 설정 파일 생성
cat > .eslintrc.json << 'EOF'
{
  "extends": [
    "react-app",
    "react-app/jest"
  ],
  "rules": {
    "no-unused-vars": "warn",
    "no-console": "warn",
    "@typescript-eslint/no-unused-vars": "warn"
  }
}
EOF
```

#### Prettier 설정
```bash
# frontend/.prettierrc
cat > frontend/.prettierrc << 'EOF'
{
  "semi": true,
  "trailingComma": "es5",
  "singleQuote": true,
  "printWidth": 80,
  "tabWidth": 2
}
EOF
```

#### Maven 품질 플러그인 추가
```xml
<!-- pom.xml에 추가 -->
<plugin>
  <groupId>org.sonarsource.scanner.maven</groupId>
  <artifactId>sonar-maven-plugin</artifactId>
  <version>3.9.1.2184</version>
</plugin>

<plugin>
  <groupId>org.jacoco</groupId>
  <artifactId>jacoco-maven-plugin</artifactId>
  <version>0.8.8</version>
  <executions>
    <execution>
      <goals>
        <goal>prepare-agent</goal>
      </goals>
    </execution>
    <execution>
      <id>report</id>
      <phase>test</phase>
      <goals>
        <goal>report</goal>
      </goals>
    </execution>
  </executions>
</plugin>
```

## 디버깅 설정

### 백엔드 디버깅

#### IDE에서 디버그 모드 실행
- IntelliJ: Debug 버튼 클릭
- VS Code: F5 키 또는 Debug 패널 사용

#### 명령줄에서 디버그 모드
```bash
# 디버그 포트 5005로 실행
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005"
```

### 프론트엔드 디버깅

#### 브라우저 개발자 도구
- Chrome DevTools 사용
- React Developer Tools 확장 프로그램 설치

#### VS Code에서 React 디버깅
```json
// .vscode/launch.json에 추가
{
  "type": "node",
  "request": "launch",
  "name": "Debug React App",
  "runtimeExecutable": "${workspaceFolder}/frontend/node_modules/.bin/react-scripts",
  "args": ["start"],
  "env": {
    "BROWSER": "none"
  },
  "console": "integratedTerminal"
}
```

## 데이터베이스 개발 도구

### DB Browser for SQLite 설치
```bash
# Ubuntu/Debian
sudo apt install sqlitebrowser

# macOS
brew install --cask db-browser-for-sqlite

# Windows: 공식 웹사이트에서 다운로드
```

### 데이터베이스 스키마 확인
```sql
-- 테이블 목록 보기
.tables

-- 테이블 구조 보기
.schema uplink_messages
.schema join_events

-- 데이터 확인
SELECT COUNT(*) FROM uplink_messages;
SELECT COUNT(*) FROM join_events;
```

## 트러블슈팅

### 자주 발생하는 문제

#### 1. 포트 충돌
```bash
# 포트 사용 중인 프로세스 확인
netstat -tulpn | grep :8081
netstat -tulpn | grep :3000

# 프로세스 종료
kill -9 <PID>
```

#### 2. 의존성 문제
```bash
# Maven 의존성 새로고침
mvn dependency:resolve -U

# npm 캐시 정리
cd frontend
rm -rf node_modules package-lock.json
npm install
```

#### 3. 데이터베이스 접근 오류
```bash
# 권한 확인
ls -la lora_gateway.db

# 권한 설정
chmod 666 lora_gateway.db
```

#### 4. CORS 오류
- 백엔드 WebConfig.java의 CORS 설정 확인
- 프론트엔드 프록시 설정 확인 (package.json의 proxy)

### 로그 확인 방법
```bash
# 백엔드 로그
tail -f logs/application.log

# Docker 컨테이너 로그
docker-compose logs -f lora-web-dashboard

# 브라우저 콘솔 로그 확인 (프론트엔드)
```