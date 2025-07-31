# LoRa Gateway Logger 문서

## 문서 개요

이 폴더는 LoRa Gateway Logger 프로젝트와 웹 애플리케이션 개발을 위한 포괄적인 문서를 포함합니다.

## 문서 목록

### 📊 데이터베이스 관련
- **[database-schema.md](./database-schema.md)** - SQLite 데이터베이스 스키마 정보
- **[database-connection.md](./database-connection.md)** - 데이터베이스 연결 설정 가이드

### 🔧 설정 및 환경
- **[port-configuration.md](./port-configuration.md)** - 포트 설정 및 네트워크 구성
- **[raspberry-pi-environment.md](./raspberry-pi-environment.md)** - 라즈베리파이 환경 설정

### 📡 LoRa 데이터
- **[lora-data-structure.md](./lora-data-structure.md)** - LoRa 데이터 구조 및 메시지 형식

### 🏗️ 프로젝트 구조
- **[project-structure.md](./project-structure.md)** - 전체 프로젝트 구조 및 모듈 설명

### 💻 개발 가이드
- **[development-guide.md](./development-guide.md)** - 개발 환경 설정 및 코딩 표준
- **[web-integration-guide.md](./web-integration-guide.md)** - Java + JavaScript 웹 애플리케이션 통합

## 빠른 시작

### 1. 프로젝트 개요 파악
먼저 [project-structure.md](./project-structure.md)를 읽어 전체 프로젝트 구조를 이해하세요.

### 2. 개발 환경 설정
[development-guide.md](./development-guide.md)를 따라 로컬 개발 환경을 구축하세요.

### 3. 데이터베이스 이해
[database-schema.md](./database-schema.md)에서 데이터 구조를 파악하고, [database-connection.md](./database-connection.md)에서 연결 방법을 확인하세요.

### 4. LoRa 데이터 이해
[lora-data-structure.md](./lora-data-structure.md)에서 LoRa 메시지 형식과 처리 방법을 학습하세요.

### 5. 웹 애플리케이션 개발
[web-integration-guide.md](./web-integration-guide.md)를 참조하여 Java + JavaScript 웹 애플리케이션을 구현하세요.

## 문서 사용 가이드

### 개발자용
- 새로운 기능 개발 시: `development-guide.md` → `project-structure.md` → 관련 기술 문서
- 버그 수정 시: `project-structure.md` → 해당 모듈 문서 → `development-guide.md`

### 운영자용
- 초기 설치: `raspberry-pi-environment.md` → `port-configuration.md` → `database-connection.md`
- 문제 해결: 각 문서의 "문제 해결" 섹션 참조

### 웹 개발자용
- 백엔드 개발: `database-schema.md` → `lora-data-structure.md` → `web-integration-guide.md`
- 프론트엔드 개발: `lora-data-structure.md` → `web-integration-guide.md`

## 기술 스택 요약

### 백엔드 (LoRa Gateway Logger)
- **언어**: Python 3.9+
- **프레임워크**: paho-mqtt
- **데이터베이스**: SQLite
- **컨테이너**: Docker

### 웹 애플리케이션
- **백엔드**: Java + Spring Boot
- **프론트엔드**: JavaScript (React 권장)
- **데이터베이스**: SQLite (공유)
- **통신**: REST API + WebSocket

### 인프라
- **MQTT 브로커**: Eclipse Mosquitto
- **플랫폼**: 라즈베리파이 (또는 Linux)
- **모니터링**: 로그 기반

## 주요 개념

### LoRaWAN 아키텍처
```
LoRa 디바이스 → LoRaWAN 게이트웨이 → 네트워크 서버 → 애플리케이션 서버
                                                                    ↓
                                                              MQTT 브로커
                                                                    ↓
                                                          LoRa Gateway Logger
                                                                    ↓
                                                            SQLite 데이터베이스
                                                                    ↓
                                                          웹 애플리케이션
```

### 데이터 흐름
1. **수집**: LoRa 디바이스에서 데이터 전송
2. **전송**: MQTT를 통한 메시지 전달
3. **처리**: Python 애플리케이션에서 파싱 및 저장
4. **저장**: SQLite 데이터베이스에 구조化 저장
5. **조회**: 웹 애플리케이션을 통한 데이터 시각화

## 문서 업데이트 가이드

### 문서 작성 원칙
1. **명확성**: 기술적 내용을 명확하게 설명
2. **실용성**: 실제 구현 가능한 예제 포함
3. **완성도**: 처음부터 끝까지 따라할 수 있는 완전한 가이드
4. **최신성**: 코드 변경시 문서도 함께 업데이트

### 문서 구조
- **개요**: 문서의 목적과 범위
- **설정**: 환경 설정 및 준비사항
- **구현**: 단계별 구현 가이드
- **예제**: 실제 사용 가능한 코드 예제
- **문제해결**: 일반적인 문제와 해결방법
- **참고자료**: 추가 학습 자료

### 기여 방법
1. 새로운 문서 추가시 이 README.md 업데이트
2. 코드 변경시 관련 문서 동시 업데이트
3. 오타나 개선사항 발견시 즉시 수정
4. 예제 코드는 실제 테스트 후 포함

## 추가 리소스

### 외부 문서
- [LoRaWAN 사양서](https://lora-alliance.org/resource_hub/lorawan-specification-v1-0-3/)
- [MQTT 프로토콜](https://mqtt.org/mqtt-specification/)
- [SQLite 문서](https://www.sqlite.org/docs.html)
- [Spring Boot 가이드](https://spring.io/guides)

### 도구 및 유틸리티
- [MQTT Explorer](http://mqtt-explorer.com/) - MQTT 메시지 모니터링
- [DB Browser for SQLite](https://sqlitebrowser.org/) - SQLite 데이터베이스 관리
- [Postman](https://www.postman.com/) - API 테스트

## 지원 및 문의

### 문제 신고
- 버그 리포트: GitHub Issues 사용
- 기능 요청: GitHub Discussions 사용
- 문서 개선: Pull Request 제출

### 개발 커뮤니티
- 기술 질문: 프로젝트 Wiki 활용
- 코드 리뷰: Pull Request 과정에서 진행
- 지식 공유: 문서 기여 및 예제 추가

---

📝 **문서 버전**: 1.0  
📅 **최종 업데이트**: 2025년 7월 31일
👥 **기여자**: 개발팀  

이 문서들이 프로젝트 개발과 웹 애플리케이션 구현에 도움이 되기를 바랍니다!