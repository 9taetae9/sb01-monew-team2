# 1기-MoNew-02팀

## 👥 팀원 구성 및 R&R

| 이름     | R&R                                                                 |
|----------|----------------------------------------------------------------------|
| [김태현<br>(팀장)](https://github.com/9taetae9) | |
| [김주언](https://github.com/wndjs803) |  |
| [연예림](https://github.com/yinneu) |  |
| [이소영](https://github.com/gitSoyoungLee) | |
| [허지웅](https://github.com/kiki1875b) |  |

---
## 📝 프로젝트 소개
**모뉴(MoNew) - PostgreSQL 및 MongoDB 기반 백업 및 복구 시스템을 갖춘 사용자 맞춤형 통합 뉴스 플랫폼**  
📆 프로젝트 기간: 2025.04.16 ~ 2025.05.13  

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
| `chore`  | 기타 변경 |

### 🔄 Git Flow
![image](https://github.com/user-attachments/assets/6ae847b8-b30d-4d5f-85dc-5c0099a3ced7)
