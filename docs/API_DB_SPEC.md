# Tagvote DB / API Specification

## Overview

- Base URL: `https://vote.newdawnsoi.site`
- Auth 방식: 세션 기반 인증
- Swagger UI: `/swagger-ui.html`
- 공개 API prefix: `/api/public/**`
- 기본 운영 DB 이름: `tagvtoeDB`

## Role Policy

| Role | 설명 |
|---|---|
| `USER` | 본인이 생성한 `vote`, 그 하위 `question` 조회/관리 가능 |
| `ADMIN` | 모든 `user`, `vote`, `question`에 대한 전체 권한 |
| 비로그인 사용자 | 공개 API만 접근 가능. `vote` 생성, 해당 `vote`의 질문+tag 조회, `tag` CRUD, `event_user` 생성 가능 |

## Common Rules

- 모든 시간 컬럼은 `created_at`, `updated_at`이며 자동 관리됩니다.
- `password`는 평문이 아니라 서버에서 암호화(BCrypt) 후 저장됩니다.
- `name`은 로그인 ID처럼 사용됩니다.
- 세션 인증이 필요한 API는 로그인 후 받은 세션 쿠키 기준으로 동작합니다.
- 익명 태그 소유권 비교 헤더는 `X-Taglow-Session-Id`입니다.

---

## DB Tables

### 1. `user`

| 컬럼명 | 타입 | NULL | PK | FK | 설명 |
|---|---|---|---|---|---|
| `id` | `BIGINT AUTO_INCREMENT` | N | Y |  | 사용자 ID |
| `name` | `VARCHAR(255)` | N |  |  | 사용자명 / 로그인 ID |
| `password` | `VARCHAR(255)` | N |  |  | 암호화된 비밀번호 |
| `created_at` | `DATETIME(6)` | N |  |  | 생성일시 |
| `updated_at` | `DATETIME(6)` | N |  |  | 수정일시 |

애플리케이션 제약:

- `name`: 필수, 공백 불가
- `password`: 필수, 공백 불가, 최소 8자, 최대 100자
- 동일한 `name`으로 회원가입 불가

### 2. `user_role`

| 컬럼명 | 타입 | NULL | PK | FK | 설명 |
|---|---|---|---|---|---|
| `user_id` | `BIGINT` | N | Y | `user.id` | 사용자 ID |
| `role` | `ENUM('ADMIN', 'USER')` | N | Y |  | 권한 |

설명:

- 복합 PK: `(user_id, role)`
- 한 사용자는 여러 권한을 가질 수 있음

### 3. `vote`

| 컬럼명 | 타입 | NULL | PK | FK | 설명 |
|---|---|---|---|---|---|
| `id` | `BIGINT AUTO_INCREMENT` | N | Y |  | 투표 ID |
| `create_user_id` | `BIGINT` | N |  | `user.id` | 생성자 ID |
| `name` | `VARCHAR(255)` | N |  |  | 투표 이름 |
| `status` | `ENUM('progress', 'end')` | N |  |  | 진행 상태 |
| `created_at` | `DATETIME(6)` | N |  |  | 생성일시 |
| `updated_at` | `DATETIME(6)` | N |  |  | 수정일시 |

애플리케이션 제약:

- `name`: 필수, 공백 불가
- `status`: 응답/요청에서는 `PROGRESS`, `END` enum으로 표현됨

### 4. `question`

| 컬럼명 | 타입 | NULL | PK | FK | 설명 |
|---|---|---|---|---|---|
| `id` | `BIGINT AUTO_INCREMENT` | N | Y |  | 질문 ID |
| `vote_id` | `BIGINT` | N |  | `vote.id` | 상위 투표 ID |
| `title` | `VARCHAR(255)` | Y |  |  | 질문 제목 |
| `detail` | `TEXT` | Y |  |  | 질문 설명 |
| `image_url` | `TEXT` | N |  |  | 질문 이미지 URL |
| `image_ratio` | `BIGINT` | N |  |  | 이미지 비율값 |
| `created_at` | `DATETIME(6)` | N |  |  | 생성일시 |
| `updated_at` | `DATETIME(6)` | N |  |  | 수정일시 |

애플리케이션 제약:

- `voteId`: 필수
- `imageUrl`: 필수, 공백 불가
- `imageRatio`: 필수, 0보다 커야 함

### 5. `tag`

| 컬럼명 | 타입 | NULL | PK | FK | 설명 |
|---|---|---|---|---|---|
| `id` | `BIGINT AUTO_INCREMENT` | N | Y |  | 태그 ID |
| `question_id` | `BIGINT` | N |  | `question.id` | 상위 질문 ID |
| `type` | `ENUM('text', 'photo', 'video')` | Y |  |  | 태그 타입 |
| `data` | `TEXT` | N |  |  | 태그 실제 데이터 |
| `duration` | `INT` | N |  |  | 지속 시간 |
| `location_x` | `FLOAT` | Y |  |  | X 좌표 |
| `location_y` | `FLOAT` | Y |  |  | Y 좌표 |
| `session_id` | `VARCHAR(100)` | Y |  |  | 익명 참여자 세션 식별자 |
| `created_at` | `DATETIME(6)` | N |  |  | 생성일시 |
| `updated_at` | `DATETIME(6)` | N |  |  | 수정일시 |

애플리케이션 제약:

- `data`: 필수, 공백 불가
- `duration`: 필수, 0 이상
- `type`: 선택값, `TEXT`, `PHOTO`, `VIDEO`
- `sessionId`: `X-Taglow-Session-Id` 헤더 값 저장

### 6. `event_user`

| 컬럼명 | 타입 | NULL | PK | FK | 설명 |
|---|---|---|---|---|---|
| `id` | `BIGINT AUTO_INCREMENT` | N | Y |  | 이벤트 참여자 ID |
| `name` | `VARCHAR(255)` | N |  |  | 이름 |
| `phone` | `VARCHAR(255)` | N |  |  | 전화번호 |
| `privacy_consent` | `TINYINT` | N |  |  | 개인정보 동의 여부 |
| `created_at` | `DATETIME(6)` | N |  |  | 생성일시 |
| `updated_at` | `DATETIME(6)` | N |  |  | 수정일시 |

애플리케이션 제약:

- `name`: 필수, 공백 불가
- `phone`: 필수, 공백 불가
- `privacyConsent`: 필수, boolean 성격

---

## API Specification

## Auth

### `POST /api/auth/login`

설명: 로그인 후 세션 생성

인증: 불필요

Request Body:

| 필드 | 타입 | 필수 | 제약 | 설명 |
|---|---|---|---|---|
| `name` | string | Y | 공백 불가 | 로그인 ID |
| `password` | string | Y | 공백 불가 | 비밀번호 |

Response 200:

| 필드 | 타입 | 설명 |
|---|---|---|
| `userId` | number | 사용자 ID |
| `name` | string | 사용자명 |
| `roles` | array<string> | 권한 목록 (`ADMIN`, `USER`) |

### `POST /api/auth/logout`

설명: 세션 로그아웃

인증: 필요

Response: `204 No Content`

### `GET /api/auth/me`

설명: 현재 로그인 사용자 조회

인증: 필요

Response 200:

| 필드 | 타입 | 설명 |
|---|---|---|
| `userId` | number | 사용자 ID |
| `name` | string | 사용자명 |
| `roles` | array<string> | 권한 목록 |

---

## User

### `POST /api/users`

설명: 회원가입

인증: 불필요

Request Body:

| 필드 | 타입 | 필수 | 제약 | 설명 |
|---|---|---|---|---|
| `name` | string | Y | 공백 불가 | 사용자명 |
| `password` | string | Y | 최소 8자, 최대 100자 | 비밀번호 |

Response 201:

| 필드 | 타입 | 설명 |
|---|---|---|
| `id` | number | 사용자 ID |
| `name` | string | 사용자명 |
| `roles` | array<string> | 기본값 `USER` |
| `createdAt` | datetime | 생성일시 |
| `updatedAt` | datetime | 수정일시 |

추가 규칙:

- `name` 중복 시 `409 Conflict`

### `GET /api/users`

설명: 전체 사용자 조회

인증: 필요 (`ADMIN`만 가능)

### `GET /api/users/me`

설명: 내 정보 조회

인증: 필요

### `GET /api/users/{userId}`

설명: 특정 사용자 조회

인증: 필요 (본인 또는 `ADMIN`)

### `PATCH /api/users/{userId}`

설명: 사용자 수정

인증: 필요 (본인 또는 `ADMIN`)

Request Body:

| 필드 | 타입 | 필수 | 제약 | 설명 |
|---|---|---|---|---|
| `name` | string | N | 값이 있으면 공백 문자열 비권장 | 사용자명 |
| `password` | string | N | 값이 있으면 최소 8자, 최대 100자 | 비밀번호 |

추가 규칙:

- `name` 변경 시 중복 이름이면 `409 Conflict`

### `PATCH /api/users/{userId}/roles`

설명: 사용자 권한 변경

인증: 필요 (`ADMIN`만 가능)

Request Body:

| 필드 | 타입 | 필수 | 제약 | 설명 |
|---|---|---|---|---|
| `roles` | array<string> | Y | 비어 있으면 안 됨 | `ADMIN`, `USER` |

### `DELETE /api/users/{userId}`

설명: 사용자 삭제

인증: 필요 (본인 또는 `ADMIN`)

Response: `204 No Content`

---

## Vote

### `POST /api/public/votes`

설명: 투표 생성

인증: 불필요

설명 메모:

| 항목 | 내용 |
|---|---|
| 공개 경로 정책 | 비로그인 접근 가능한 API라서 `/api/public/**` 아래에 위치 |

Request Body:

| 필드 | 타입 | 필수 | 제약 | 설명 |
|---|---|---|---|---|
| `createdByUserId` | number | N | 비로그인 생성 시 필수 | 투표 생성자 사용자 ID |
| `name` | string | Y | 공백 불가 | 투표명 |

추가 규칙:

- `createdByUserId`에 해당하는 사용자가 없으면 `404 Not Found`
- 로그인 없이 생성 가능하지만 `createdByUserId`는 반드시 필요
- 로그인 세션이 있으면 `createdByUserId` 대신 현재 세션 사용자 ID가 우선 사용됨

### `GET /api/votes`

설명: 접근 가능한 투표 목록 조회

인증: 필요

권한 규칙:

- `ADMIN`: 전체 조회
- `USER`: 본인이 생성한 투표만 조회

### `GET /api/votes/{voteId}`

설명: 특정 투표 조회

인증: 필요

권한 규칙:

- `ADMIN`: 전체 접근 가능
- `USER`: 본인 소유 투표만 가능

### `PATCH /api/votes/{voteId}`

설명: 투표 수정

인증: 필요

Request Body:

| 필드 | 타입 | 필수 | 제약 | 설명 |
|---|---|---|---|---|
| `name` | string | N | 값이 있으면 공백 문자열 비권장 | 투표명 |
| `status` | string | N | `PROGRESS`, `END` | 상태 |

### `DELETE /api/votes/{voteId}`

설명: 투표 삭제

인증: 필요

Response: `204 No Content`

### `GET /api/public/votes/{voteId}/display`

설명: 스텐바이미 화면용. 특정 투표의 모든 질문 + 각 질문의 태그 목록 조회

인증: 불필요

Response 200:

| 필드 | 타입 | 설명 |
|---|---|---|
| `voteId` | number | 투표 ID |
| `voteName` | string | 투표명 |
| `status` | string | `PROGRESS`, `END` |
| `questions` | array | 질문+태그 목록 |

`questions[]` 구조:

| 필드 | 타입 | 설명 |
|---|---|---|
| `question` | object | 질문 정보 |
| `tags` | array | 해당 질문에 속한 태그 목록 |

요청 헤더:

| 헤더 | 필수 | 설명 |
|---|---|---|
| `X-Taglow-Session-Id` | N | 있으면 응답 내 태그의 `isMine`, `canDelete` 계산에 사용 |

### `GET /api/public/votes/{voteId}/events`

설명: 새 태그 생성 이벤트 SSE 구독

인증: 불필요

응답 형식:

- Event name: `connected`
- Event name: `tag-created`

`tag-created` data 구조:

| 필드 | 타입 | 설명 |
|---|---|---|
| `voteId` | number | 투표 ID |
| `questionId` | number | 질문 ID |
| `tag` | object | 생성된 태그 정보 |

요청 헤더:

| 헤더 | 필수 | 설명 |
|---|---|---|
| `X-Taglow-Session-Id` | N | 있으면 새 태그 이벤트의 `isMine`, `canDelete` 계산에 사용 |

---

## Question

### `POST /api/questions`

설명: 질문 생성

인증: 필요

Request Body:

| 필드 | 타입 | 필수 | 제약 | 설명 |
|---|---|---|---|---|
| `voteId` | number | Y |  | 상위 투표 ID |
| `title` | string | N |  | 제목 |
| `detail` | string | N |  | 설명 |
| `imageUrl` | string | Y | 공백 불가 | 이미지 URL |
| `imageRatio` | number | Y | 0보다 커야 함 | 이미지 비율 |

권한 규칙:

- `ADMIN`: 전체 접근 가능
- `USER`: 본인 소유 `vote`에만 생성 가능

### `GET /api/votes/{voteId}/questions`

설명: 특정 투표의 질문 + 태그 목록 조회

인증: 필요

권한 규칙:

- `ADMIN`: 전체 접근 가능
- `USER`: 본인 소유 `vote`만 가능

### `GET /api/questions/{questionId}`

설명: 특정 질문 + 태그 목록 조회

인증: 필요

### `PATCH /api/questions/{questionId}`

설명: 질문 수정

인증: 필요

Request Body:

| 필드 | 타입 | 필수 | 제약 | 설명 |
|---|---|---|---|---|
| `title` | string | N |  | 제목 |
| `detail` | string | N |  | 설명 |
| `imageUrl` | string | N | 값이 있으면 공백 문자열 비권장 | 이미지 URL |
| `imageRatio` | number | N | 값이 있으면 0보다 커야 함 | 이미지 비율 |

### `DELETE /api/questions/{questionId}`

설명: 질문 삭제

인증: 필요

Response: `204 No Content`

### `GET /api/public/votes/{voteId}/questions`

설명: 특정 투표의 질문 목록과 각 질문의 태그 목록 조회

인증: 불필요

Response `200 OK`: `QuestionWithTagsResponse[]`

요청 헤더:

| 헤더 | 필수 | 설명 |
|---|---|---|
| `X-Taglow-Session-Id` | N | 있으면 각 태그의 `isMine`, `canDelete` 계산에 사용 |

---

## Tag

### `POST /api/public/questions/{questionId}/tags`

설명: 태그 생성

인증: 불필요

Request Body:

| 필드 | 타입 | 필수 | 제약 | 설명 |
|---|---|---|---|---|
| `questionId` | number | Y | 형식상 필수 | 현재 구현에서는 Path Variable이 실제 기준 |
| `type` | string | N | `TEXT`, `PHOTO`, `VIDEO` | 태그 타입 |
| `data` | string | Y | 공백 불가 | 태그 데이터 |
| `duration` | number | Y | 0 이상 | 지속 시간 |
| `locationX` | number | N |  | X 좌표 |
| `locationY` | number | N |  | Y 좌표 |

추가 규칙:

- 실제 질문 식별은 URL의 `{questionId}` 기준으로 처리됨
- 생성 성공 시 해당 투표 SSE 구독자에게 `tag-created` 이벤트 발행

요청 헤더:

| 헤더 | 필수 | 설명 |
|---|---|---|
| `X-Taglow-Session-Id` | Y | 태그 소유 세션 식별자. 태그 생성 시 함께 저장됨 |

### `GET /api/public/questions/{questionId}/tags`

설명: 특정 질문의 태그 목록 조회

인증: 불필요

요청 헤더:

| 헤더 | 필수 | 설명 |
|---|---|---|
| `X-Taglow-Session-Id` | N | 있으면 각 태그의 `isMine`, `canDelete` 계산에 사용 |

### `GET /api/public/tags/{tagId}`

설명: 특정 태그 조회

인증: 불필요

요청 헤더:

| 헤더 | 필수 | 설명 |
|---|---|---|
| `X-Taglow-Session-Id` | N | 있으면 `isMine`, `canDelete` 계산에 사용 |

### `PATCH /api/public/tags/{tagId}`

설명: 태그 수정

인증: 불필요

Request Body:

| 필드 | 타입 | 필수 | 제약 | 설명 |
|---|---|---|---|---|
| `type` | string | N | `TEXT`, `PHOTO`, `VIDEO` | 태그 타입 |
| `data` | string | N | 값이 있으면 공백 문자열 비권장 | 태그 데이터 |
| `duration` | number | N | 0 이상 | 지속 시간 |
| `locationX` | number | N |  | X 좌표 |
| `locationY` | number | N |  | Y 좌표 |

요청 헤더:

| 헤더 | 필수 | 설명 |
|---|---|---|
| `X-Taglow-Session-Id` | N | 있으면 수정 응답의 `isMine`, `canDelete` 계산에 사용 |

### `DELETE /api/public/tags/{tagId}`

설명: 태그 삭제

인증: 불필요

Response: `204 No Content`

요청 헤더:

| 헤더 | 필수 | 설명 |
|---|---|---|
| `X-Taglow-Session-Id` | Y | 저장된 `sessionId`와 일치해야 삭제 가능 |

추가 규칙:

- 다른 세션이 만든 태그 삭제 요청은 `403 Forbidden`

---

## Event User

### `POST /api/public/event-users`

설명: 이벤트 참여자 생성

인증: 불필요

Request Body:

| 필드 | 타입 | 필수 | 제약 | 설명 |
|---|---|---|---|---|
| `name` | string | Y | 공백 불가 | 이름 |
| `phone` | string | Y | 공백 불가 | 전화번호 |
| `privacyConsent` | boolean | Y | null 불가 | 개인정보 동의 여부 |

Response 201:

| 필드 | 타입 | 설명 |
|---|---|---|
| `id` | number | 참여자 ID |
| `name` | string | 이름 |
| `phone` | string | 전화번호 |
| `privacyConsent` | boolean | 동의 여부 |
| `createdAt` | datetime | 생성일시 |
| `updatedAt` | datetime | 수정일시 |

---

## Response Object Summary

### `VoteResponse`

| 필드 | 타입 |
|---|---|
| `id` | number |
| `createdByUserId` | number |
| `name` | string |
| `status` | `PROGRESS` \| `END` |
| `isMine` | boolean |
| `createdAt` | datetime |
| `updatedAt` | datetime |

### `QuestionResponse`

| 필드 | 타입 |
|---|---|
| `id` | number |
| `voteId` | number |
| `title` | string \| null |
| `detail` | string \| null |
| `imageUrl` | string |
| `imageRatio` | number |
| `createdAt` | datetime |
| `updatedAt` | datetime |

### `TagResponse`

| 필드 | 타입 |
|---|---|
| `id` | number |
| `questionId` | number |
| `type` | `TEXT` \| `PHOTO` \| `VIDEO` \| null |
| `data` | string |
| `duration` | number |
| `locationX` | number \| null |
| `locationY` | number \| null |
| `isMine` | boolean |
| `canDelete` | boolean |
| `createdAt` | datetime |
| `updatedAt` | datetime |
