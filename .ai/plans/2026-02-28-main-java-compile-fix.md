# Plan: Main Java Compile Fix

Date: 2026-02-28  
Status: Completed

## Goal
- `src/Main.java`의 컴파일 오류를 제거해 기본 Java 진입점 형태로 정리한다.

## Scope
- 포함:
  - `src/Main.java`를 클래스 기반 엔트리포인트로 수정
  - 출력 API를 표준 Java API로 교체
  - 관련 문서 동기화
- 제외:
  - 서버 런타임 로직(`l1j.server.*`) 변경
  - DB/API/네트워크 프로토콜 계약 변경
  - 빌드 시스템 구조 변경

## Strategy
1. 컴파일 오류 원인(`void main`, `IO.println`)을 표준 Java 구조로 치환한다.
2. `public class Main` + `public static void main(String[] args)` 형태로 정리한다.
3. 변경 내역을 `.ai/tasks`, `.ai/current_task`, `.ai/current_state`에 동기화한다.

## Impact Analysis
- 영향받는 모듈:
  - 샘플/엔트리 파일 (`src/Main.java`)
- 영향받는 인터페이스(API/DB/Contract):
  - 없음
- 아키텍처 레이어 영향:
  - 없음

## Risks
- 로컬 환경에 JDK/Maven 미설치 시 즉시 컴파일 검증이 제한된다.

## Contract Compatibility
- 기존 계약 변경 없음.
