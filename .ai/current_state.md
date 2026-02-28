# Current State

Last verified: 2026-02-28

## Repository facts
- `.ai` governance directory exists and is now composed of `README.md`, `current_state.md`, `current_task.md`, `decisions.md`, `plans/`, and `tasks/`.
- Root build/start files include `build.xml` and `ServerStart.bat`.
- Root also includes `pom.xml` for Maven build.
- `src/Main.java` defines `public class Main` with `public static void main(String[] args)`.
- `src/Main.java` uses `System.out.println` for sample output.

## Build and runtime facts
- `build.xml` defines an Ant project named `L1J` with default target `all` -> `clean`, `compile`, `jar`, `clean2`.
- `build.xml` compiles Java sources from `src` to `build` with encoding `utf-8`.
- `build.xml` XML declaration encoding is `UTF-8`.
- `build.xml` packages `l1jserver.jar` and references `src/META-INF/MANIFEST.MF` as the manifest path.
- Current repository scan did not find `src/META-INF/MANIFEST.MF`.
- `build.xml` classpath includes `c3p0-0.9.1.1.jar`, `javolution.jar`, and `mysql-connector-java-5.1.8-bin.jar`.
- `ServerStart.bat` starts `l1j.server.Leaf` with classpath entries including `mysql-connector-java-5.1.5-bin.jar`.
- `pom.xml` defines Maven coordinates `l1j.server:l1jserver:1.0.0-SNAPSHOT`.
- `pom.xml` sets source directory to `src` and source encoding to `UTF-8`.
- `pom.xml` maps dependencies: `com.mchange:c3p0:0.9.1.1`, `javolution:javolution:5.5.1`, `mysql:mysql-connector-java:5.1.8`.
- `pom.xml` configures JAR manifest main class as `l1j.server.Leaf`.
- `pom.xml` excludes `Main.java` from Maven compilation.

## Source code facts
- `src/l1j/server/Server.java` contains `public static void main(final String[] args)` and initializes `Config`, `L1DatabaseFactory`, and `GameServer`.
- `src/l1j/server/Leaf.java` also contains a `public static void main(String args[])` entry point and AWT/Swing UI code.
- `src/l1j/server/Leaf.java` loads logging config from classpath `/config/log.properties` and falls back to `./config/log.properties` when classpath resource is unavailable.
- A recursive count reports `674` Java files under `src/l1j`.
- A recursive count reports `654` Java files under `src/l1j/server/server`.
- `src/l1j/server/server` contains these top-level subpackages: `clientpackets`, `command`, `datatables`, `encryptions`, `model`, `serverpackets`, `storage`, `taskmanager`, `templates`, `types`, `utils`.
- On 2026-02-22, text sources in `src` (extensions: `.java`, `.xml`, `.properties`, `.mf`, `.txt`) were normalized to UTF-8:
  - scanned `675`, converted `419`, already valid UTF-8 `256`, failed `0`
  - strict UTF-8 validation after migration: `invalid_utf8=0`

## Configuration facts
- `config/server.properties` defines `GameserverPort=2000`.
- `config/server.properties` defines DB properties including:
  - `Driver=com.mysql.jdbc.Driver`
  - `URL=jdbc:mysql://localhost/L1JDB? useUnicode=true&characterEncoding=euckr`
  - `Login=root`
  - `Password=` (empty in file)
- `config/server.properties` contains additional runtime toggles such as `GeneralThreadPoolType`, `SkillTimerImplType`, and `NpcAIImplType`.
