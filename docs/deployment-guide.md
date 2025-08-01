# 배포 가이드

## 배포 방법 개요

LoRa Web Dashboard는 다음과 같은 방법으로 배포할 수 있습니다:

1. **Docker Compose** (권장)
2. **개별 서비스 배포**
3. **클라우드 배포**

## Docker Compose 배포 (권장)

### 1. 사전 준비

#### 필수 요구사항
- Docker 20.10+
- Docker Compose 2.0+
- 최소 2GB RAM
- 최소 5GB 디스크 공간

#### 데이터베이스 파일 준비
```bash
# LoRa Gateway Logger에서 생성된 DB 파일을 복사
cp /path/to/lora_gateway.db ./lora_gateway.db

# 또는 빈 DB 파일 생성 (테스트용)
touch lora_gateway.db
```

### 2. Docker Compose 실행

#### 전체 스택 배포
```bash
# 백그라운드에서 모든 서비스 실행
docker-compose up -d

# 로그 확인
docker-compose logs -f

# 서비스 상태 확인
docker-compose ps
```

#### 최소 구성 배포 (웹앱 + MQTT만)
```bash
# LoRa Gateway Logger 없이 실행
docker-compose -f docker-compose-minimal.yml up -d
```

### 3. 접속 확인

- **웹 대시보드**: http://localhost:3001
- **백엔드 API**: http://localhost:8081
- **헬스체크**: http://localhost:8081/health

### 4. 서비스 관리

```bash
# 서비스 중지
docker-compose down

# 서비스 재시작
docker-compose restart

# 특정 서비스만 재시작
docker-compose restart lora-web-dashboard

# 로그 보기
docker-compose logs -f lora-web-dashboard

# 컨테이너 내부 접속
docker-compose exec lora-web-dashboard bash
```

## 개별 서비스 배포

### 백엔드 배포

#### 1. Java 환경 설정
```bash
# Java 17 설치 확인
java -version

# Maven 설치 확인
mvn -version
```

#### 2. 애플리케이션 빌드
```bash
# JAR 파일 생성
mvn clean package -DskipTests

# 생성된 JAR 파일 확인
ls -la target/*.jar
```

#### 3. 애플리케이션 실행
```bash
# 기본 실행
java -jar target/lora-web-dashboard-1.0.0.jar

# 프로파일 지정 실행
java -jar target/lora-web-dashboard-1.0.0.jar --spring.profiles.active=prod

# 환경변수 설정하여 실행
export SPRING_DATASOURCE_URL=jdbc:sqlite:/app/lora_gateway.db
export SERVER_PORT=8081
java -jar target/lora-web-dashboard-1.0.0.jar
```

#### 4. 서비스 등록 (Linux)

**systemd 서비스 파일 생성:**
```bash
sudo nano /etc/systemd/system/lora-web-dashboard.service
```

```ini
[Unit]
Description=LoRa Web Dashboard
After=network.target

[Service]
Type=simple
User=lora
ExecStart=/usr/bin/java -jar /opt/lora-web-dashboard/lora-web-dashboard-1.0.0.jar
WorkingDirectory=/opt/lora-web-dashboard
Environment=SPRING_PROFILES_ACTIVE=prod
Environment=SPRING_DATASOURCE_URL=jdbc:sqlite:/opt/lora-web-dashboard/lora_gateway.db
Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target
```

**서비스 활성화:**
```bash
sudo systemctl daemon-reload
sudo systemctl enable lora-web-dashboard
sudo systemctl start lora-web-dashboard
sudo systemctl status lora-web-dashboard
```

### 프론트엔드 배포

#### 1. 프로덕션 빌드
```bash
cd frontend

# 의존성 설치
npm install

# 프로덕션 빌드
npm run build

# 빌드 결과 확인
ls -la build/
```

#### 2. Nginx 설정

**Nginx 설치:**
```bash
# Ubuntu/Debian
sudo apt update
sudo apt install nginx

# CentOS/RHEL
sudo yum install nginx
```

**Nginx 설정 파일:**
```bash
sudo nano /etc/nginx/sites-available/lora-web-dashboard
```

```nginx
server {
    listen 80;
    server_name your-domain.com;  # 실제 도메인으로 변경
    
    root /var/www/lora-web-dashboard;
    index index.html;
    
    # 정적 파일 서빙
    location / {
        try_files $uri $uri/ /index.html;
        
        # 캐시 설정
        location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg)$ {
            expires 1y;
            add_header Cache-Control "public, immutable";
        }
    }
    
    # API 프록시
    location /api/ {
        proxy_pass http://localhost:8081/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
    
    # WebSocket 프록시
    location /ws {
        proxy_pass http://localhost:8081/ws;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
    
    # Gzip 압축
    gzip on;
    gzip_vary on;
    gzip_min_length 1024;
    gzip_types text/plain text/css text/xml text/javascript application/javascript application/xml+rss application/json;
}
```

**파일 배포 및 서비스 활성화:**
```bash
# 빌드 파일 복사
sudo cp -r frontend/build/* /var/www/lora-web-dashboard/

# 사이트 활성화
sudo ln -s /etc/nginx/sites-available/lora-web-dashboard /etc/nginx/sites-enabled/
sudo nginx -t
sudo systemctl reload nginx
```

## 클라우드 배포

### AWS 배포

#### 1. EC2 인스턴스 설정
```bash
# Docker 설치
sudo yum update -y
sudo yum install -y docker
sudo service docker start
sudo usermod -a -G docker ec2-user

# Docker Compose 설치
sudo curl -L "https://github.com/docker/compose/releases/download/v2.20.0/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose
```

#### 2. 보안 그룹 설정
- HTTP (80): 0.0.0.0/0
- HTTPS (443): 0.0.0.0/0  
- Custom TCP (3001): 0.0.0.0/0 (웹 대시보드)
- Custom TCP (8081): 0.0.0.0/0 (API)
- SSH (22): 본인 IP만

#### 3. 애플리케이션 배포
```bash
# 소스코드 클론
git clone https://github.com/your-repo/lora-web-dashboard.git
cd lora-web-dashboard

# 데이터베이스 파일 업로드
scp -i your-key.pem lora_gateway.db ec2-user@your-ec2-ip:~/lora-web-dashboard/

# Docker Compose 실행
docker-compose up -d
```

### Azure Container Instances

#### 1. Azure CLI 설정
```bash
# Azure CLI 로그인
az login

# 리소스 그룹 생성
az group create --name lora-dashboard-rg --location eastus
```

#### 2. 컨테이너 이미지 빌드 및 푸시
```bash
# Azure Container Registry 생성
az acr create --resource-group lora-dashboard-rg --name loradashboardacr --sku Basic

# Docker 이미지 빌드
docker build -t lora-web-dashboard .

# ACR에 푸시
az acr login --name loradashboardacr
docker tag lora-web-dashboard loradashboardacr.azurecr.io/lora-web-dashboard:latest
docker push loradashboardacr.azurecr.io/lora-web-dashboard:latest
```

#### 3. Container Instance 생성
```bash
az container create \
  --resource-group lora-dashboard-rg \
  --name lora-web-dashboard \
  --image loradashboardacr.azurecr.io/lora-web-dashboard:latest \
  --dns-name-label lora-dashboard \
  --ports 80 8081 \
  --environment-variables SPRING_PROFILES_ACTIVE=prod
```

## SSL/TLS 설정

### Let's Encrypt 인증서

#### 1. Certbot 설치
```bash
# Ubuntu/Debian
sudo apt install certbot python3-certbot-nginx

# CentOS/RHEL
sudo yum install certbot python3-certbot-nginx
```

#### 2. 인증서 발급
```bash
# 자동 Nginx 설정
sudo certbot --nginx -d your-domain.com

# 수동 설정
sudo certbot certonly --webroot -w /var/www/lora-web-dashboard -d your-domain.com
```

#### 3. 자동 갱신 설정
```bash
# crontab 편집
sudo crontab -e

# 매월 1일 새벽 2시에 갱신 시도
0 2 1 * * /usr/bin/certbot renew --quiet && /usr/bin/systemctl reload nginx
```

## 모니터링 및 로깅

### 1. 로그 수집
```bash
# Docker Compose 로그
docker-compose logs -f --tail=100

# 특정 서비스 로그
docker-compose logs -f lora-web-dashboard

# 로그 파일 위치 (서비스 배포시)
tail -f /var/log/lora-web-dashboard/application.log
```

### 2. 헬스체크 모니터링
```bash
# 스크립트 작성
cat > healthcheck.sh << 'EOF'
#!/bin/bash
HEALTH_URL="http://localhost:8081/health"
RESPONSE=$(curl -s -o /dev/null -w "%{http_code}" $HEALTH_URL)

if [ $RESPONSE -eq 200 ]; then
    echo "$(date): Service is healthy"
else
    echo "$(date): Service is unhealthy (HTTP $RESPONSE)"
    # 알림 또는 재시작 로직 추가
fi
EOF

chmod +x healthcheck.sh

# cron으로 5분마다 실행
echo "*/5 * * * * /path/to/healthcheck.sh >> /var/log/healthcheck.log 2>&1" | crontab -
```

### 3. 리소스 모니터링
```bash
# 시스템 리소스 확인
docker-compose exec lora-web-dashboard top
docker stats

# 디스크 사용량
df -h
du -sh lora_gateway.db
```

## 백업 및 복구

### 1. 데이터베이스 백업
```bash
# 백업 스크립트
cat > backup_db.sh << 'EOF'
#!/bin/bash
BACKUP_DIR="/backup/lora-dashboard"
DATE=$(date +%Y%m%d_%H%M%S)
DB_FILE="lora_gateway.db"

mkdir -p $BACKUP_DIR
cp $DB_FILE $BACKUP_DIR/lora_gateway_backup_$DATE.db

# 7일 이상 된 백업 파일 삭제
find $BACKUP_DIR -name "lora_gateway_backup_*.db" -mtime +7 -delete

echo "Backup completed: $BACKUP_DIR/lora_gateway_backup_$DATE.db"
EOF

chmod +x backup_db.sh

# 매일 새벽 2시에 백업
echo "0 2 * * * /path/to/backup_db.sh >> /var/log/backup.log 2>&1" | crontab -
```

### 2. 복구
```bash
# 백업에서 복구
cp /backup/lora-dashboard/lora_gateway_backup_20240101_020000.db lora_gateway.db

# 서비스 재시작
docker-compose restart lora-web-dashboard
```

## 성능 최적화

### 1. JVM 튜닝
```bash
# Java 실행 옵션
JAVA_OPTS="-Xms512m -Xmx2g -XX:+UseG1GC -XX:G1HeapRegionSize=16m"
java $JAVA_OPTS -jar target/lora-web-dashboard-1.0.0.jar
```

### 2. 데이터베이스 최적화
```sql
-- 인덱스 최적화
PRAGMA optimize;

-- WAL 모드 활성화 (성능 향상)
PRAGMA journal_mode=WAL;

-- 메모리 설정
PRAGMA cache_size=10000;
```

### 3. Nginx 캐싱
```nginx
# 정적 파일 캐싱
location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg|woff|woff2)$ {
    expires 1y;
    add_header Cache-Control "public, immutable";
    add_header Vary Accept-Encoding;
}

# API 응답 캐싱 (선택적)
location /api/statistics {
    proxy_pass http://localhost:8081/statistics;
    proxy_cache_valid 200 5m;
    add_header X-Cache-Status $upstream_cache_status;
}
```