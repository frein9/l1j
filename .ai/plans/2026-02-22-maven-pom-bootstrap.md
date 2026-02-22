# Plan: Maven POM Bootstrap

Date: 2026-02-22  
Status: Completed

## Goal
- 현재 Ant 기반 Java 서버 프로젝트를 Maven으로도 빌드할 수 있도록 루트에 `pom.xml`을 추가한다.

## Scope
- 포함:
  - `pom.xml` 신규 생성
  - 기존 의존성(`c3p0`, `javolution`, `mysql-connector-java`) 반영
  - 컴파일 인코딩 `UTF-8` 반영
  - 현재 소스 구조(`src`) 반영 및 컴파일 불가 샘플 파일 제외
- 제외:
  - 런타임 로직/게임 서버 기능 수정
  - DB 스키마/프로토콜/API 계약 변경
  - 기존 Ant 스크립트 제거

## Strategy
1. `build.xml`과 실행 배치 파일 기준으로 의존성과 메인 클래스를 정렬한다.
2. Maven 기본 구조와 다른 현재 소스 트리(`src`)를 `sourceDirectory`로 명시한다.
3. `src/Main.java`는 컴파일 오류 유발 파일이므로 컴파일 대상에서 제외한다.
4. 문서(`task/current_task/current_state/README/decisions`)를 동기화한다.

## Impact Analysis
- 영향받는 모듈:
  - 빌드 레이어 (`pom.xml`)
- 영향받는 인터페이스(API/DB/Contract):
  - 없음
- 아키텍처 레이어 영향:
  - 없음 (빌드 도구 추가)

## Risks
- 매우 오래된 의존성 버전은 Maven 중앙 저장소 해상도 문제가 있을 수 있다.

## Contract Compatibility
- 기존 런타임 계약 변경 없음.
