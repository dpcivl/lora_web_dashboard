# 트러블슈팅 가이드

## 일반적인 문제 및 해결책

### 🚀 애플리케이션 시작 문제

#### 1. 백엔드 시작 실패

**증상:** Spring Boot 애플리케이션이 시작되지 않음

**가능한 원인 및 해결책:**

```bash
# 원인 1: 포트 8081이 이미 사용 중
# 확인
netstat -tulpn | grep :8081
# 또는 Windows에서
netstat -an | findstr :8081

# 해결: 사용 중인 프로세스 종료
kill -9 <PID>
# 또는 다른 포트 사용
java -jar target/lora-web-dashboard-1.0.0.jar --server.port=8082
```

```bash
# 원인 2: Java 버전 불일치
# 확인
java -version

# 해결: Java 17 설치 및 설정
export JAVA_HOME=/path/to/java17
```

```bash
# 원인 3: 데이터베이스 파일 접근 권한 문제
# 확인
ls -la lora_gateway.db

# 해결: 권한 수정
chmod 666 lora_gateway.db
# 또는 파일 생성
touch lora_gateway.db
```

#### 2. 프론트엔드 시작 실패

**증상:** React 개발 서버가 시작되지 않음

```bash
# 원인 1: Node.js 버전 문제
# 확인
node --version

# 해결: Node.js 18+ 설치
nvm install 18
nvm use 18
```

```bash
# 원인 2: 의존성 설치 문제
# 해결: 캐시 정리 후 재설치
cd frontend
rm -rf node_modules package-lock.json
npm cache clean --force
npm install
```

```bash
# 원인 3: 포트 3000 충돌
# 확인
netstat -tulpn | grep :3000

# 해결: 다른 포트 사용
PORT=3001 npm start
```

### 🗄️ 데이터베이스 관련 문제

#### 1. SQLite 연결 실패

**증상:** `org.sqlite.SQLiteException: [SQLITE_CANTOPEN]`

```bash
# 원인 1: 데이터베이스 파일이 존재하지 않음
# 해결: 빈 파일 생성
touch lora_gateway.db

# 또는 기존 파일 복사
cp /path/to/source/lora_gateway.db ./
```

```bash
# 원인 2: 디렉터리 권한 문제
# 해결: 권한 확인 및 수정
ls -la ./
chmod 755 .
chmod 666 lora_gateway.db
```

#### 2. JPA/Hibernate 오류

**증상:** `Table doesn't exist` 오류

```sql
-- 해결: 테이블 수동 생성
sqlite3 lora_gateway.db

CREATE TABLE IF NOT EXISTS uplink_messages (
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

CREATE TABLE IF NOT EXISTS join_events (
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

.exit
```

### 🌐 네트워크 및 API 문제

#### 1. CORS 오류

**증상:** 브라우저 콘솔에 CORS 오류 메시지

```java
// 해결: WebConfig.java 설정 확인
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000", "http://localhost:3001")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
```

#### 2. API 응답 없음

**증상:** 프론트엔드에서 "통계 데이터를 불러올 수 없습니다" 오류

```bash
# 확인 1: 백엔드 상태 체크
curl http://localhost:8081/health

# 확인 2: API 엔드포인트 테스트
curl http://localhost:8081/messages/statistics

# 확인 3: 네트워크 연결
telnet localhost 8081
```

```typescript
// 프론트엔드 API 클라이언트 설정 확인
// frontend/src/api/client.ts
const API_BASE_URL = process.env.REACT_APP_API_BASE_URL || 'http://localhost:8081';

// 타임아웃 설정 추가
axios.defaults.timeout = 10000;
```

#### 3. WebSocket 연결 실패

**증상:** 실시간 데이터 업데이트가 작동하지 않음

```javascript
// 확인: WebSocket 연결 상태
// 브라우저 개발자 도구 → Network → WS 탭 확인

// 해결: WebSocket 재연결 로직 추가
const useWebSocket = (url) => {
  const [socket, setSocket] = useState(null);
  
  useEffect(() => {
    const connectWebSocket = () => {
      const ws = new WebSocket(url);
      
      ws.onopen = () => {
        console.log('WebSocket 연결됨');
        setSocket(ws);
      };
      
      ws.onclose = () => {
        console.log('WebSocket 연결 끊어짐, 재연결 시도...');
        setTimeout(connectWebSocket, 3000);
      };
      
      ws.onerror = (error) => {
        console.error('WebSocket 오류:', error);
      };
    };
    
    connectWebSocket();
  }, [url]);
  
  return socket;
};
```

### 🐳 Docker 관련 문제

#### 1. Docker 빌드 실패

**증상:** `docker-compose up` 실행 시 빌드 오류

```bash
# 원인 1: package-lock.json 누락
# 해결: 프론트엔드 의존성 먼저 설치
cd frontend
npm install
cd ..
docker-compose build --no-cache
```

```bash
# 원인 2: Docker 이미지 캐시 문제
# 해결: 캐시 제거 후 재빌드
docker system prune -a
docker-compose build --no-cache
```

#### 2. 컨테이너 시작 실패

**증상:** 컨테이너가 시작 후 즉시 종료됨

```bash
# 로그 확인
docker-compose logs lora-web-dashboard

# 컨테이너 상태 확인
docker-compose ps

# 컨테이너 내부 확인
docker-compose exec lora-web-dashboard bash
```

```yaml
# docker-compose.yml 볼륨 권한 문제 해결
volumes:
  - ./lora_gateway.db:/app/lora_gateway.db:rw
  - ./logs:/app/logs:rw
```

#### 3. 포트 충돌

**증상:** `Port already in use` 오류

```bash
# 사용 중인 포트 확인
docker-compose ps
netstat -tulpn | grep -E ":(3001|8081|1883)"

# 해결: 포트 변경
# docker-compose.yml에서 포트 수정
ports:
  - "3002:80"      # 3001 대신 3002 사용
  - "8082:8081"    # 8081 대신 8082 사용
```

### 📊 데이터 및 차트 문제

#### 1. 차트가 표시되지 않음

**증상:** 대시보드에서 차트가 빈 화면으로 표시됨

```javascript
// 원인: Chart.js 의존성 문제
// 해결: Chart.js 재설치
cd frontend
npm uninstall chart.js react-chartjs-2
npm install chart.js react-chartjs-2

// 또는 차트 컴포넌트에서 에러 처리 추가
const HourlyMessagesChart = ({ data }) => {
  if (!data || data.length === 0) {
    return <div>차트 데이터가 없습니다.</div>;
  }
  
  // 차트 렌더링 로직
};
```

#### 2. 통계 데이터가 0으로 표시됨

**증상:** 모든 통계가 0으로 표시됨

```sql
-- 확인: 데이터베이스에 실제 데이터가 있는지 체크
sqlite3 lora_gateway.db
SELECT COUNT(*) FROM uplink_messages;
SELECT COUNT(*) FROM join_events;

-- 최근 데이터 확인
SELECT * FROM uplink_messages ORDER BY created_at DESC LIMIT 5;
```

```java
// 백엔드 서비스 로직 확인
@Service
public class MessageService {
    public StatisticsDto getStatistics() {
        // 로그 추가하여 디버깅
        log.debug("통계 조회 시작");
        
        long totalMessages = messageRepository.count();
        log.debug("전체 메시지 수: {}", totalMessages);
        
        // 나머지 로직...
    }
}
```

### 🔧 성능 문제

#### 1. 페이지 로딩이 느림

**증상:** 웹 페이지 로딩 시간이 5초 이상 소요됨

```sql
-- 데이터베이스 최적화
sqlite3 lora_gateway.db
PRAGMA optimize;
VACUUM;
ANALYZE;

-- 인덱스 확인
.schema
-- 필요시 인덱스 추가
CREATE INDEX IF NOT EXISTS idx_uplink_messages_device_timestamp 
ON uplink_messages(device_id, timestamp);
```

```java
// JPA 쿼리 최적화
@Repository
public interface UplinkMessageRepository extends JpaRepository<UplinkMessage, Long> {
    
    // 페이징 쿼리 최적화
    @Query("SELECT m FROM UplinkMessage m ORDER BY m.timestamp DESC")
    Page<UplinkMessage> findAllOrderByTimestampDesc(Pageable pageable);
    
    // N+1 문제 해결
    @Query("SELECT m FROM UplinkMessage m JOIN FETCH m.device WHERE m.deviceId = :deviceId")
    List<UplinkMessage> findByDeviceIdWithDevice(@Param("deviceId") String deviceId);
}
```

#### 2. 메모리 사용량 증가

**증상:** 애플리케이션 메모리 사용량이 지속적으로 증가

```bash
# JVM 힙 덤프 생성
jcmd <PID> GC.run_finalization
jcmd <PID> VM.gc

# 메모리 사용량 모니터링
docker stats

# JVM 옵션 조정
JAVA_OPTS="-Xms512m -Xmx1g -XX:+UseG1GC"
```

### 🔍 로깅 및 디버깅

#### 로그 레벨 조정

```yaml
# application.yml
logging:
  level:
    com.lora.dashboard: DEBUG
    org.springframework.web: DEBUG
    org.hibernate.SQL: DEBUG
    org.hibernate.type.descriptor.sql.BasicBinder: TRACE
```

#### 상세 로그 확인

```bash
# 실시간 로그 모니터링
tail -f logs/application.log

# 특정 오류 검색
grep -i "error\|exception" logs/application.log

# Docker 컨테이너 로그
docker-compose logs -f --tail=100 lora-web-dashboard
```

## 진단 체크리스트

### 시스템 상태 확인

```bash
#!/bin/bash
# health-check.sh

echo "=== LoRa Web Dashboard 상태 점검 ==="

# 1. 포트 확인
echo "1. 포트 상태 확인:"
netstat -tulpn | grep -E ":(3000|3001|8081|1883)" || echo "   포트가 열려있지 않음"

# 2. 프로세스 확인  
echo "2. 프로세스 상태:"
ps aux | grep -E "(java|node)" | grep -v grep

# 3. 데이터베이스 파일 확인
echo "3. 데이터베이스 파일:"
if [ -f "lora_gateway.db" ]; then
    echo "   ✓ 데이터베이스 파일 존재"
    echo "   크기: $(du -h lora_gateway.db | cut -f1)"
else
    echo "   ✗ 데이터베이스 파일 없음"
fi

# 4. API 상태 확인
echo "4. API 상태:"
curl -s http://localhost:8081/health | grep -q "UP" && echo "   ✓ API 정상" || echo "   ✗ API 오류"

# 5. 디스크 공간 확인
echo "5. 디스크 공간:"
df -h . | tail -1

echo "=== 점검 완료 ==="
```

### 성능 모니터링

```bash
#!/bin/bash
# performance-monitor.sh

echo "=== 성능 모니터링 ==="

# CPU 사용률
echo "1. CPU 사용률:"
top -bn1 | grep "Cpu(s)" | awk '{print $2}' | sed 's/%us,//'

# 메모리 사용률
echo "2. 메모리 사용률:"
free -h

# Java 프로세스 메모리
echo "3. Java 프로세스 메모리:"
ps aux | grep java | grep -v grep | awk '{print $6/1024 " MB"}'

# 데이터베이스 크기
echo "4. 데이터베이스 크기:"
du -sh lora_gateway.db

# 네트워크 연결
echo "5. 네트워크 연결:"
netstat -an | grep -E ":(8081|3001)" | wc -l
```

## 자주 묻는 질문 (FAQ)

### Q1: "통계 데이터를 불러올 수 없습니다" 오류가 계속 발생합니다.

**A:** 다음 순서로 확인하세요:
1. 백엔드 서버가 실행 중인지 확인: `curl http://localhost:8081/health`
2. 데이터베이스에 데이터가 있는지 확인
3. CORS 설정 확인
4. 브라우저 개발자 도구에서 네트워크 탭 확인

### Q2: Docker 컨테이너가 계속 재시작됩니다.

**A:** 컨테이너 로그를 확인하세요:
```bash
docker-compose logs lora-web-dashboard
```
주로 메모리 부족이나 데이터베이스 접근 권한 문제입니다.

### Q3: 프론트엔드에서 실시간 데이터가 업데이트되지 않습니다.

**A:** WebSocket 연결을 확인하세요:
1. 브라우저 개발자 도구 → Network → WS 탭
2. WebSocket 연결 상태 확인
3. 방화벽이나 프록시 설정 확인

### Q4: 차트가 표시되지 않습니다.

**A:** Chart.js 관련 문제일 가능성이 높습니다:
```bash
cd frontend
npm install chart.js react-chartjs-2 --save
npm start
```

---

💡 **도움이 필요하신가요?**
- GitHub Issues에 문제 상황과 로그를 함께 올려주세요
- 시스템 환경 정보 (OS, Java 버전, Node.js 버전)도 함께 제공해주세요