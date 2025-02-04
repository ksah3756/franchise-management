# Development Branch Convention

## Branch Naming Convention
- `main` : 배포 브랜치
- `dev` : 개발 브랜치
- `feature/{feature-name}` : 새로운 기능 개발 브랜치
- `refactor/{refactor-name}` : 코드 리팩토링 브랜치
- `fix/{bug-name}` : 버그 수정 브랜치
- `hotfix/{hotfix-name}` : 긴급 수정 브랜치

### Branch Naming Examples
```plaintext
feature/user-login
refactor/database-optimization
fix/order-api-bug
hotfix/critical-security-issue
```

## Commit Message Convention
Commit 메시지는 `타입: 내용` 형식으로 작성합니다.

### Commit Type
- **feat** : 새로운 기능 추가
- **fix** : 버그 수정
- **refactor** : 코드 리팩토링 (기능 변경 없음)
- **docs** : 문서 수정 (README 등)
- **test** : 테스트 코드 추가/수정
- **chore** : 기타 변경사항 (빌드, 패키지 매니저 설정 등)
- **style** : 코드 스타일 수정 (세미콜론 추가, 들여쓰기 등)
- **perf** : 성능 개선

### Commit Message Example
```plaintext
feat: 사용자 로그인 기능 추가
fix: 결제 API 호출 시 null 오류 수정
refactor: ProductService 코드 리팩토링
