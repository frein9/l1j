# Plan: Encoding UTF-8 Migration

Date: 2026-02-22  
Status: Completed

## Goal
- `src` 소스 파일의 텍스트 인코딩을 UTF-8로 통일하고 빌드 인코딩 설정을 UTF-8로 정렬한다.

## Scope
- 포함:
  - `src` 하위 텍스트 소스(`.java`, `.xml`, `.properties`, `.mf`, `.txt`)의 인코딩 변환
  - `build.xml`의 `javac` 인코딩 설정을 `utf-8`로 변경
- 제외:
  - 바이너리 파일 및 리소스 데이터(`maps`, `data/mapcache`, `emblem` 등)
  - DB 스키마/런타임 기능 로직 변경

## Strategy
1. 파일별 UTF-8 유효성 검사를 통해 이미 UTF-8인 파일은 유지한다.
2. UTF-8이 아닌 파일만 EUC-KR로 디코드 후 UTF-8(무 BOM)으로 재인코딩한다.
3. 빌드 인코딩 설정을 UTF-8로 변경한다.
4. 변경 건수 및 영향 범위를 검증하고 task/current_state를 동기화한다.

## Impact Analysis
- 영향받는 모듈:
  - `src` 텍스트 소스 전반
  - 빌드 스크립트(`build.xml`)
- 영향받는 인터페이스(API/DB/Contract):
  - 없음 (인코딩/빌드 설정 레벨 변경)
- 아키텍처 레이어 영향:
  - 없음 (레이어/의존성 구조 변화 없음)

## Risks
- 파일이 EUC-KR이 아닌 다른 인코딩일 경우 문자 깨짐 가능성.
- 이를 줄이기 위해 UTF-8 유효성 검사 후 비-UTF-8 파일만 변환.

## Contract Compatibility
- 네트워크/API/DB 계약 변경 없음.
