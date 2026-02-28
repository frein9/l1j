# Plan: DBMS Selectable FileDB Migration

Date: 2026-02-28  
Status: In Progress

## Goal
- 기존 비즈니스 로직/SQL 호출 흐름은 최대한 유지하면서, DBMS를 `mysql` 또는 `filedb`로 선택해 실행할 수 있게 만든다.
- 기본 운영 DB를 파일 기반 DB로 전환 가능한 경로를 제공한다.

## Scope
- 포함:
  - 설정 옵션 추가: `DBMS=mysql|filedb`
  - DB 초기화 계층에서 DBMS별 연결 설정 분기
  - 파일 기반 DB 드라이버/URL 구성 추가
  - 파일 기반 DB 최초 기동 시 스키마/초기 데이터 적용 경로 정의
  - 마이그레이션/롤백 절차 수립
- 제외:
  - 게임 규칙/도메인 로직 변경
  - 패킷/프로토콜/API 계약 변경
  - 대규모 DAO 전면 개편

## Current Facts (for planning)
- 데이터 접근은 대부분 `L1DatabaseFactory.getInstance().getConnection()`에 집중되어 있다.
- `Config.load()`는 현재 `Driver/URL/Login/Password`를 `config/server.properties`에서 읽는다.
- `L1DatabaseFactory`는 c3p0 + JDBC 구조이므로 DBMS 선택을 초기화 계층에서 흡수할 수 있다.

## Technical Direction
- FileDB 후보는 **H2 file mode + MySQL compatibility mode**를 1순위로 채택한다.
  - 예시 URL: `jdbc:h2:file:./data/filedb/l1jdb;MODE=MySQL;DATABASE_TO_LOWER=TRUE;AUTO_SERVER=TRUE`
- 이유:
  - 현재 코드가 JDBC/SQL 중심이며 MySQL 문법 의존이 크기 때문에, 호환 모드 DB가 로직 변경 최소화에 유리하다.
- 대안:
  - SQLite는 SQL 호환 이슈(DDL/DML 함수/타입 차이)로 보정 범위가 커질 가능성이 높아 1차 목표와 충돌.

## Option Design
1. `config/server.properties`에 아래 키 추가
   - `DBMS=mysql|filedb`
   - `FileDBPath=./data/filedb/l1jdb`
   - `FileDBAutoInit=true`
2. 동작 규칙
   - `DBMS=mysql`:
     - 기존 `Driver/URL/Login/Password` 사용 (하위호환 100%)
   - `DBMS=filedb`:
     - 내부적으로 FileDB 전용 Driver/URL/User/Password를 설정
     - 필요 시 기존 키(`Driver/URL/...`)는 무시 또는 fallback

## Implementation Phases
1. Phase 1: 설정/부트스트랩 분기
   - `Config`에 `DBMS`, `FileDBPath`, `FileDBAutoInit` 로딩 추가
   - `Server`/`Leaf`의 DB 초기화 직전에 DBMS 분기 적용
   - `L1DatabaseFactory.setDatabaseSettings(...)` 입력값을 DBMS별로 구성
2. Phase 2: 의존성/런타임 준비
   - FileDB(H2) 의존성 추가
   - 파일 DB 저장 폴더 생성/권한 점검
3. Phase 3: 스키마 부트스트랩
   - `db/l1jdb.sql` 기반으로 FileDB용 초기 스키마 스크립트 분리(`db/filedb/init.sql`)
   - MySQL 비호환 구문 최소 치환 가이드 수립
4. Phase 4: 호환성 검증
   - 서버 기동/로그인/캐릭터 생성/아이템 저장/재기동 복구 핵심 시나리오 테스트
   - SQL 에러 로그 수집 후 호환 스크립트 보정
5. Phase 5: 전환 운영
   - 기본값을 `mysql` 유지한 채 선택 전환 제공
   - 안정화 후 운영 환경에서 `DBMS=filedb` 전환

## Data Migration Strategy
- 초기 전환은 2트랙으로 제공:
  1) 신규 서버: FileDB 초기화 스크립트로 새로 시작
  2) 기존 MySQL 데이터: 덤프/정규화/적재 스크립트 별도 제공
- 데이터 이관은 일괄 전환보다는 핵심 테이블 우선(계정/캐릭터/인벤토리/클랜)으로 단계화

## Impact Analysis
- 영향받는 모듈:
  - `Config`, `Server`, `Leaf`, `L1DatabaseFactory`, DB 초기화 스크립트/문서
- 영향받는 인터페이스(API/DB/Contract):
  - 외부 API/프로토콜 계약 변화 없음
  - 내부 DBMS 선택 계약(`DBMS` 설정 키) 신규 추가
- 아키텍처 레이어 영향:
  - 인프라 레이어(DB 연결/설정) 확장
  - 도메인/서비스 레이어 로직 변경 최소화

## Risks
- MySQL 전용 SQL 문법이 FileDB 호환 모드에서 일부 실패할 수 있음
- 인덱스/타입/문자셋 차이로 런타임 쿼리 동작 편차 가능
- 초기 데이터 이관 시 null/default 처리 차이로 데이터 정합성 이슈 가능

## Risk Mitigation
- `DBMS=mysql` 기본값 유지로 즉시 롤백 가능 구조 보장
- 단계별 테스트 케이스와 SQL 에러 수집 루프 운영
- 호환 스크립트는 MySQL 원본과 분리 관리

## Rollback Plan
- 설정값을 `DBMS=mysql`로 되돌리고 기존 `Driver/URL/Login/Password` 사용
- FileDB 관련 의존성/스크립트는 남겨두되 비활성화

## Definition of Done
- 동일 바이너리에서 `DBMS=mysql`/`DBMS=filedb` 선택 기동 가능
- `DBMS=mysql`에서 기존 동작 회귀 없음
- `DBMS=filedb`에서 핵심 시나리오(기동/로그인/저장/재기동) 통과

## Progress
- 완료:
  - `Config`에 `DBMS`, `FileDBPath`, `FileDBLogin`, `FileDBPassword` 반영
  - `Config`에 `FileDBAutoInit` 반영
  - `DBMS=filedb`일 때 H2 File URL 자동 구성 로직 추가
  - `pom.xml`에 H2 의존성 추가
  - `config/server.properties`에 DBMS 선택 옵션 추가 및 기본값을 `filedb`로 설정
  - `data/filedb` 경로 준비
  - `db/filedb/init.sql` 생성 (`db/l1jdb.sql` 기반 H2 호환 변환본)
  - `Server`/`Leaf` 기동 시 FileDB 자동 초기화(`FileDbInitializer`) 연결
- 미완료:
  - 핵심 시나리오 검증 및 호환 SQL 보정
