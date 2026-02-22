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
