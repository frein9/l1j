# Decisions Log

## 2026-02-22 - 문서 운영 체계 부트스트랩
- 배경:
  - `.ai/README.md`에는 문서 운영 규칙이 정의되어 있었지만, 필수 구성요소(`current_state.md`, `plans/`, `tasks/`, `decisions.md`)가 아직 구성되어 있지 않았다.
- 결정:
  - 문서 운영 헌법의 필수 구조를 즉시 생성하고, 이후 모든 변경은 날짜 기반 `plan/task`와 동기화하여 관리한다.
- 근거:
  - 헌법 1.1, 2.1~2.4, 3, 4 항목에서 필수 구조 및 절차를 명시한다.
- 영향:
  - 코드 실행 로직, API, DB 스키마는 변경하지 않는다.
  - 문서 운영 프로세스에 `plan/task/current_state/decision` 동기화가 강제된다.
- 결과:
  - 문서 없는 코드 변경을 방지하는 최소 운영 기반이 마련되었다.

## 2026-02-22 - Maven 빌드 정의(pom.xml) 추가
- 배경:
  - 기존 저장소는 `build.xml`(Ant) 중심으로만 빌드 정의가 존재했다.
  - Maven 기반 빌드/의존성 관리 진입점이 필요했다.
- 결정:
  - 루트에 `pom.xml`을 추가하고 기존 Ant 의존성(c3p0, javolution, mysql-connector-java)을 Maven 의존성으로 매핑한다.
  - 기존 실행 배치 기준에 맞춰 JAR 매니페스트 `mainClass`를 `l1j.server.Leaf`로 설정한다.
  - 현재 `src/Main.java`가 컴파일 불가 형태이므로 Maven 컴파일 대상에서 제외한다.
- 근거:
  - `build.xml`의 클래스패스/인코딩 설정과 `ServerStart.bat`의 실행 진입점을 기준으로 정렬.
- 영향:
  - 빌드 레이어에 Maven이 추가되며, 런타임 로직/API/DB 계약에는 변경이 없다.
- 결과:
  - Ant와 병행 가능한 Maven 빌드 진입점이 확보되었다.

## 2026-02-28 - DBMS 선택형 구조 및 FileDB(H2) 채택
- 배경:
  - 기존 서버는 `Config` + `L1DatabaseFactory`를 통해 MySQL 단일 구성으로 동작하고 있었다.
  - 운영 환경에서 DBMS를 선택해 사용할 수 있도록 전환 요구가 생겼다.
- 결정:
  - `config/server.properties`에 `DBMS` 옵션을 추가하고 `mysql`/`filedb`를 선택 가능하게 한다.
  - `DBMS=filedb`일 때 파일 기반 DB는 H2(file mode, MySQL compatibility mode)를 사용한다.
  - 기존 `L1DatabaseFactory` 호출 구조는 유지하고, `Config`에서 Driver/URL/User/Password를 DBMS별로 결정한다.
- 근거:
  - 현재 코드베이스는 JDBC 연결 지점이 `L1DatabaseFactory`로 집중되어 있어 설정 계층 흡수가 변경 범위를 최소화한다.
  - H2의 `MODE=MySQL`이 기존 SQL과의 호환 리스크를 상대적으로 낮춘다.
- 영향:
  - 인프라 레이어(설정/연결) 확장
  - 도메인 로직, 패킷/API, DB 접근 호출부의 직접 수정은 최소화
- 결과:
  - 동일 코드베이스에서 DBMS 선택 기반 실행 경로가 마련되었다.
  - FileDB 자동 초기화 경로(`FileDBAutoInit`, `FileDbInitializer`)와 초기 스키마 스크립트(`db/filedb/init.sql`)가 추가되었다.
  - 통합 검증 및 호환 SQL 보정은 후속 작업으로 남는다.
