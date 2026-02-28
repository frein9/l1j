# Task: DBMS Selectable FileDB Migration

Date: 2026-02-28  
Linked Plan: `.ai/plans/2026-02-28-dbms-selectable-filedb-migration.md`

## Checklist
- [x] 계획 수립(현행 구조 분석 반영)
- [x] `Config`에 DBMS 선택 옵션 추가
- [x] DB 부트스트랩 분기(mysql/filedb) 구현
- [x] FileDB(H2) 의존성 추가 및 URL/경로 규칙 확정
- [x] FileDB 초기 스키마/시드 스크립트 작성
- [x] FileDB 자동 초기화(`FileDBAutoInit`) 경로 구현
- [x] 전환/롤백 가이드 문서화
- [ ] 회귀 테스트 및 핵심 시나리오 검증

## Notes
- 현재 단계는 선택형 DBMS + FileDB 초기화 기반 구현 완료 상태이며, 통합 검증이 남아있다.
- 초기 구현에서 빈 `.mv.db` 생성 후 초기화가 스킵되는 조건 이슈를 수정했다(파일 존재 기반 -> 핵심 테이블 존재 기반).
- 초기화 기준을 필수 테이블 집합 검증으로 강화했고, 부분 초기화 DB 복구를 위해 init SQL 실행 시 이미 존재하는 객체/중복 데이터 오류를 무시하고 계속 진행하도록 보완했다.
- H2에서 스키마 전역 인덱스명 충돌(`key_id`)이 발생해 초기화가 중단되는 문제를 수정하기 위해, `CREATE TABLE` 실행 전 보조 인덱스명을 테이블별 고유명으로 자동 치환하도록 보완했다.
- 데이터 이관 중 일부 레거시 값 길이 초과(`22001`)로 초기화가 중단되는 문제를 방지하기 위해, 비치명 오류로 분류해 해당 statement를 건너뛰고 계속 진행하도록 보완했다.
