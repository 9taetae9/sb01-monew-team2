# 모뉴 (MoNew) - 사용자 맞춤형 통합 뉴스 플랫폼
#### 📈 Test Coverage 
[![codecov](https://codecov.io/gh/9taetae9/sb01-monew-team2/graph/badge.svg?token=T3PWUF31Y6)](https://codecov.io/gh/9taetae9/sb01-monew-team2)

---
## 📝 프로젝트 소개
**모뉴(MoNew)** 는 MongoDB 및 PostgreSQL 기반 백업 및 복구 시스템을 갖춘 사용자 맞춤형 통합 뉴스 플랫폼입니다. 다양한 뉴스 API를 통합하여 사용자에게 맞춤형 뉴스를 제공하고, 의견을 나눌 수 있는 소셜 기능을 갖추고 있습니다.

- 📆 **프로젝트 기간**: 2025.04.16 ~ 2025.05.11
- 🔗 **배포 링크**: [http://15.165.92.142/login](http://15.165.92.142/login)
- 🎬 **시연 영상**: [YouTube 시연 영상 보기](https://youtu.be/QGx8xnzYtDg)
- 📋 **협업 문서**: [Notion 페이지](https://www.notion.so/MoNew-1c7c5631fb0180c9a06eea3d4f599b7c)
- 📊 **칸반 보드**: [GitHub Projects](https://github.com/users/9taetae9/projects/3)
- 📘 **Swagger API 문서**: [v2.3.1](https://github.com/9taetae9/sb01-monew-team2/blob/release-v2.3.1/monew/docs/swagger-v2.3.1.json)
- 📑 **발표 자료**: [Canva 발표 자료](https://www.canva.com/design/DAGmFTBmxzI/W_GSg6Aw2kgVw4iNGCgthw/view?utm_content=DAGmFTBmxzI&utm_campaign=designshare&utm_medium=link2&utm_source=uniquelinks&utlId=h35c7fe3d01)

## 🛠️ 기술 스택

### 백엔드
![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![Spring Web](https://img.shields.io/badge/Spring_Web-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![Spring Batch](https://img.shields.io/badge/Spring_Batch-6DB33F?style=for-the-badge&logo=spring&logoColor=white)

### 데이터 액세스
![Spring Data JPA](https://img.shields.io/badge/Spring_Data_JPA-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![Spring Data MongoDB](https://img.shields.io/badge/Spring_Data_MongoDB-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![QueryDSL](https://img.shields.io/badge/QueryDSL-007ACC?style=for-the-badge&logo=java&logoColor=white)
![JDBC](https://img.shields.io/badge/JDBC-007396?style=for-the-badge&logo=java&logoColor=white)

### 데이터베이스
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge&logo=postgresql&logoColor=white)
![MongoDB](https://img.shields.io/badge/MongoDB-47A248?style=for-the-badge&logo=mongodb&logoColor=white)

### 클라우드/인프라
![AWS S3](https://img.shields.io/badge/AWS_S3-569A31?style=for-the-badge&logo=amazons3&logoColor=white)
![AWS ECS](https://img.shields.io/badge/AWS_ECS-FF9900?style=for-the-badge&logo=amazonecs&logoColor=white)
![AWS ECR](https://img.shields.io/badge/AWS_ECR-232F3E?style=for-the-badge&logo=amazonaws&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)

### 코드 품질 및 생산성
![Lombok](https://img.shields.io/badge/Lombok-BC4521?style=for-the-badge&logo=java&logoColor=white)
![MapStruct](https://img.shields.io/badge/MapStruct-8A2BE2?style=for-the-badge&logo=java&logoColor=white)
![JaCoCo](https://img.shields.io/badge/JaCoCo-F58025?style=for-the-badge&logo=jacoco&logoColor=white)

### 테스트
![JUnit Jupiter](https://img.shields.io/badge/JUnit_Jupiter-25A162?style=for-the-badge&logo=junit5&logoColor=white)
![Spring Batch Test](https://img.shields.io/badge/Spring_Batch_Test-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![TestContainers](https://img.shields.io/badge/TestContainers-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![H2](https://img.shields.io/badge/H2_Database-0074BD?style=for-the-badge&logo=h2&logoColor=white)

### 모니터링/관리
![Spring Boot Admin](https://img.shields.io/badge/Spring_Boot_Admin-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![Spring Actuator](https://img.shields.io/badge/Actuator-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![Micrometer](https://img.shields.io/badge/Micrometer-326CE5?style=for-the-badge&logo=prometheus&logoColor=white)

### 기타 유틸리티
![Jackson XML](https://img.shields.io/badge/Jackson_XML-0076C0?style=for-the-badge&logo=json&logoColor=white)
![Apache Commons](https://img.shields.io/badge/Apache_Commons-D22128?style=for-the-badge&logo=apache&logoColor=white)

## 👥 팀원 구성 및 R&R

| 이름 | 역할 및 기여 |
|------|------------|
| [김태현(팀장)](https://github.com/9taetae9) | • 프로젝트 관리(칸반보드 연동, 이슈 기반 브랜치 자동 생성, 이슈 템플릿)<br>• CI 파이프라인 구축<br>• 기사 백업 & 복구 시스템 구현<br>• logback 롤링 정책 및 시스템, JVM 레벨 타임존 설정<br>• 댓글 관리 도메인 설계<br>• 인프라 관리(swap 파일, JVM 옵션 최적화로 OOM 문제 해결)<br>• 릴리즈 버전 문서 관리 |
| [김주언](https://github.com/wndjs803) | • 사용자, 활동 내역 도메인 설계<br>• Fetch Join을 활용한 쿼리 성능 개선<br>• Spring Event를 활용한 클래스 간 결합도 감소<br>• MongoDB를 활용한 조회 최적화<br>• DB Schema, MongoDB 관리 |
| [연예림](https://github.com/yinneu) | • Interest 도메인 설계<br>• Subscription 도메인 설계<br>• Swagger API 문서화(자동화 워크플로우 구축)<br>• 공통 예외 처리 및 전역 핸들러 구현 <br>• Fetch Join, Index를 활용한 쿼리 최적화 |
| [이소영](https://github.com/gitSoyoungLee) | • Notification 도메인 설계<br>• 오래된 알림 삭제 배치 작업 구현<br>• QueryDsl 기반 모든 도메인 커서 페이지네이션 구현<br>• AOP 기반 쿼리 추적 로그 시스템<br>• Spring Actuator 및 커스텀 메트릭 적용<br>• Spring Boot Admin Server 적용 및 모니터링 |
| [허지웅](https://github.com/kiki1875b) | • Article 도메인 설계<br>• 기사 수집, 분류 및 저장 배치 프로세스 구현<br>• Template Method, Strategy 패턴을 통한 확장성 확보<br>• 인메모리 KeywordCache 도입<br>• CD 파이프라인 구축<br>• GIN 인덱스, tsvector를 활용한 Article 도메인 성능 개선 |

## 🏗️ 시스템 아키텍처
![시스템 아키텍처](https://github.com/user-attachments/assets/b910bcdd-0fdc-41a0-86e5-5609a8ea2013)

모뉴 시스템은 다음과 같은 아키텍처로 구성되어 있습니다.

1. **CI/CD 파이프라인**
  - GitHub을 통한 코드 관리
  - GitHub Actions를 활용한 자동화된 테스트 및 배포
  - AWS ECR을 통한 컨테이너 이미지 관리

2. **백엔드 시스템**
  - Amazon EC2 인스턴스에서 애플리케이션 실행
  - MongoDB와 PostgreSQL을 활용한 데이터 저장
  - AWS S3를 활용한 로그 및 기사 백업 관리

3. **데이터베이스 구조**
  - PostgreSQL: 관계형 데이터를 위한 주 데이터베이스
  - MongoDB: 사용자 활동 내역과 같은 조회 최적화를 위한 비관계형 데이터베이스

## 📁 프로젝트 구조
```
monew/                                # 루트 디렉토리
├── admin/                            # Spring Boot Admin 애플리케이션
│   ├── Dockerfile                    # Admin 컨테이너화 설정
│   ├── build.gradle                  
│   └── src/
│       ├── main/java/.../monew/
│       │   └── AdminApplication.java # Admin 진입점
│       └── test/java/.../monew/
│           └── AdminApplicationTests.java
│
├── src/                              
│   ├── main/                         # 메인 소스 코드
│   │   └── java/.../monew/
│   │       ├── config/               # 애플리케이션 설정
│   │       │   ├── JpaConfig.java    # JPA 설정
│   │       │   ├── MongoDbConfig.java# MongoDB 설정
│   │       │   ├── WebConfig.java    # 웹 관련 설정
│   │       │   ├── interceptor/      # 인터셉터
│   │       │   └── queryAspect/      # 쿼리 추적 AOP
│   │       │
│   │       └── module/               # 애플리케이션 모듈
│   │           ├── actuator/         # 모니터링 지표
│   │           ├── common/           # 공통 모듈 (예외 처리 등)
│   │           ├── domain/           # 도메인 모듈
│   │           │   ├── article/      # 기사 도메인
│   │           │   │   ├── backup/   # 기사 백업 & 복구
│   │           │   │   ├── batch/    # 기사 수집 배치
│   │           │   │   └── controller, dto, entity, repository, service...
│   │           │   │
│   │           │   ├── comment/      # 댓글 도메인
│   │           │   ├── interest/     # 관심사 도메인
│   │           │   ├── notification/ # 알림 도메인
│   │           │   ├── subscription/ # 구독 도메인
│   │           │   ├── user/         # 사용자 도메인
│   │           │   └── useractivity/ # 사용자 활동 도메인
│   │           │
│   │           └── log/              # 로그 관리
│   │
│   └── test/                         # 테스트 소스 코드
│       └── java/.../monew/
│           ├── config/               # 설정 테스트
│           └── module/
│               ├── TestEntityFactory.java
│               ├── actuator/         # 모니터링 지표 테스트
│               ├── domain/           # 도메인별 테스트
│               │   ├── article/      # 기사 도메인 테스트
│               │   │   ├── backup/   # 백업 & 복구 테스트
│               │   │   ├── batch/    # 배치 테스트
│               │   │   ├── controller/
│               │   │   ├── repository/
│               │   │   └── service/
│               │   │
│               │   ├── comment/      # 댓글 도메인 테스트
│               │   ├── interest/     # 관심사 도메인 테스트
│               │   ├── notification/ # 알림 도메인 테스트
│               │   ├── subscription/ # 구독 도메인 테스트
│               │   ├── user/         # 사용자 도메인 테스트
│               │   └── useractivity/ # 사용자 활동 도메인 테스트
│               │
│               └── log/              # 로그 관리 테스트
│
├── docs/                             # API 문서 및 관련 문서
├── logs/                             # 로그 파일 디렉토리
├── gradle/                           
├── build.gradle                      
└── Dockerfile                        # 메인 애플리케이션 컨테이너화 설정
```

## 💾 데이터베이스 스키마

### PostgreSQL
<img src="https://github.com/user-attachments/assets/0f0c3559-b9c7-4e59-a2c7-5858a3cefd76" width="650" alt="PostgreSQL ERD">

### MongoDB
<img src="https://github.com/user-attachments/assets/f95215c8-46d4-43c7-b47c-9584f080b018" width="650" alt="MongoDB 스키마">

## 🚀 주요 기능

### 사용자 관리
- 회원가입, 로그인, 닉네임 수정 기능
- 논리적 삭제 지원으로 데이터 무결성 유지
- 보안을 위한 비밀번호 암호화

### 관심사 관리
- 관심사 등록, 수정, 삭제 기능
- 키워드 기반 관심사 설정
- 관심사 구독 시스템
- 유사도 기반 중복 관심사 방지

### 뉴스 기사 관리
- 다양한 출처(Naver API, RSS 피드 등)를 통한 뉴스 기사 수집
- 관심사 키워드 기반 필터링
- 백업 및 복구 시스템 구현
- 조회수, 댓글 수 등 다양한 정렬 옵션

### 댓글 관리
- 기사별 댓글 등록, 수정, 삭제
- 좋아요 기능
- 커서 기반 페이지네이션

### 활동 내역 관리
- MongoDB를 활용한 사용자 활동 내역 조회 최적화
- 구독 중인 관심사, 최근 작성 댓글, 좋아요, 조회 기사 추적

### 알림 관리
- 관심사 관련 새 기사 알림
- 댓글 좋아요 알림
- 알림 확인 및 자동 삭제 배치 처리

## 📊 성능 최적화

### 쿼리 최적화
- Fetch Join을 활용한 N+1 문제 해결
- 인덱스 전략 적용으로 조회 성능 개선
- GIN 인덱스 및 tsvector를 활용한 전문 검색 최적화

### MongoDB를 활용한 조회 최적화
- 사용자 활동 내역을 위한 비정규화 모델 설계
- 조인 없이 빠른 조회가 가능한 문서 구조 설계

### 메모리 최적화
- 인메모리 KeywordCache 도입으로 기사 분류 성능 개선
- swap 공간 생성 및 JVM 옵션 최적화로 OOM 문제 해결

### 배치 프로세스 최적화
- Template Method, Strategy 패턴을 통한 확장성 있는 배치 구조
- 청크 기반 처리로 대용량 데이터 효율적 관리

## 🔍 모니터링 및 로깅

### Spring Boot Admin
- 애플리케이션 상태 모니터링 대시보드
- 스프링 액추에이터 통합으로 런타임 정보 확인
![image](https://github.com/user-attachments/assets/3946a669-4de0-4e3c-bb54-0c30fee34dde)


### 커스텀 메트릭
- 배치 작업 관련 커스텀 메트릭 수집
- Micrometer를 통한 메트릭 노출
![image](https://github.com/user-attachments/assets/5bfd9f65-1dd7-4ab8-b4de-8f718efee123)

### 쿼리 추적 로그
- AOP를 활용한 HTTP 요청별 쿼리 실행 수와 시간 로깅
- 개발 환경에서 쿼리 최적화를 위한 도구로 활용
```
Query Statistics: URL = GET /api/interests,  Query Count = 3, Query Time = 22(ms)
```
### 로그 & 백업 관리
- Logback을 활용한 로그 롤링 정책 구현
- 로그 파일 압축 및 S3 업로드 자동화
- 일별 기사 S3 백업
<table>
  <tr>
    <td rowspan="2" align="center" valign="middle">
      <img src="https://github.com/user-attachments/assets/3dc0c216-009e-4f11-9712-2393ad4ed0e5" width="350px" alt="로그 압축 및 S3 업로드">
    </td>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/958c0f4e-4f7d-434e-9aa5-cce7c782720d" width="350px" alt="로그 시스템 구성도">
    </td>
  </tr>
  <tr>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/e0c4b559-dd96-4a38-8ce1-ef6d460c391b" width="350px" alt="로그 관리 시스템 구조">
    </td>
  </tr>
</table>

## 🌿 Branch Naming Convention

**형식**: `[유형]/#[이슈번호]/[키워드]`

| 라벨 유형    | 접두사   | 예시                                   | 비고                     |
|--------------|----------|----------------------------------------|--------------------------|
| `feat`       | `feat`   | `feat/#123/login-modal`                | 신기능 개발             |
| `hotfix`     | `hotfix` | `hotfix/#45/auth-error`                | 긴급 수정               |
| `refactor`   | `feat`   | `feat/#89/employee-search-refactor`    | 리팩토링 작업           |
| `etc.`  | `task`   | `task/#234/docs-update`                | 문서 작업 등 일반 작업  |

### ✨ 주요 규칙
- 영문 소문자 사용 (employ-search ⭕ / EmploySearch ❌)
- 하이픈(-) 으로 단어 구분
- 이슈 번호는 #+숫자 형식
- 리팩토링 작업 시 기존 feat 브랜치명에 '-refactor' 접미사 추가
- 키워드 길이 20자 이내 권장

## 💬 Commit Message Convention
**형식**: `유형(범위): <설명> #이슈번호`
- 브랜치의 첫 커밋과 마지막 커밋에는 #이슈번호 필수로 포함하기
- 작은 변경도 자주 커밋하여 기록하기
- 다른 이슈와 연관된 작업 시 커밋 예
  - ex) refactor(A,B): 공통 코드 분리 #1#4

| 타입     | 사용 시나리오 |
|----------|---------------|
| `feat`   | 신기능 추가 |
| `fix`    | 버그 수정 |
| `refactor`| 성능 개선 및 코드 리팩토링, 파일 또는 디렉토리명 수정, 경로 변경 |
| `test`	 | 테스트 코드, 리팩토링 테스트 코드 추가 |
| `style`	 | 코드 포멧팅 |
| `comment`	 | 주석 추가 및 수정 |
| `docs`	 | 문서 수정 |
| `remove`	 | 파일 삭제 |
| `chore`  | 기타 변경 |

### 🔄 Git Flow
![image](https://github.com/user-attachments/assets/6ae847b8-b30d-4d5f-85dc-5c0099a3ced7)
