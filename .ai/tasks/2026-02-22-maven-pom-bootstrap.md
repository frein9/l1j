# Task: Maven POM Bootstrap

Date: 2026-02-22  
Linked Plan: `.ai/plans/2026-02-22-maven-pom-bootstrap.md`

## Checklist
- [x] plan 파일 생성
- [x] task 파일 생성
- [x] `pom.xml` 생성
- [x] 의존성/인코딩/소스 경로 반영
- [x] `src/Main.java` 컴파일 제외 반영
- [x] 문서 동기화(`current_task`, `current_state`, `README`, `decisions`)
- [x] 아키텍처 보호 규칙 점검

## Execution Result
- 루트 `pom.xml` 신규 생성
- 소스 경로를 `src`로 지정
- 인코딩을 `UTF-8`로 지정
- 메인 클래스 매니페스트를 `l1j.server.Leaf`로 지정
- 컴파일 제외: `src/Main.java`
