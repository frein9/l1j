# Plan: Leaf Log Properties Load Fix

Date: 2026-02-28  
Status: Completed

## Goal
- `l1j.server.Leaf` 실행 시 `log.properties` 로딩 실패로 발생하는 NPE를 제거한다.

## Scope
- 포함:
  - `src/l1j/server/Leaf.java`의 로그 설정 로딩 로직 보완
  - 리소스 경로 실패 시 파일 경로(`./config/log.properties`) fallback 추가
  - NPE 방지를 위한 null 체크 및 안전한 스트림 종료
  - 문서 동기화
- 제외:
  - 게임 서버 기능/프로토콜 변경
  - DB 스키마/계약 변경
  - 빌드 시스템 구조 변경

## Strategy
1. 기존 리소스 로딩(`/config/log.properties`)을 유지한다.
2. 로딩 실패(null) 시 `./config/log.properties` 파일 입력 스트림으로 대체한다.
3. 스트림 null 여부를 검사하고 try-with-resources로 안전하게 처리한다.
4. `.ai` 문서를 현재 코드 상태에 맞게 동기화한다.

## Impact Analysis
- 영향받는 모듈:
  - `l1j.server.Leaf` (부트스트랩 로그 초기화)
- 영향받는 인터페이스(API/DB/Contract):
  - 없음
- 아키텍처 레이어 영향:
  - 없음

## Risks
- `config/log.properties` 자체가 누락된 환경에서는 로그 설정이 기본값으로 동작할 수 있다.

## Contract Compatibility
- 기존 계약 변경 없음.
