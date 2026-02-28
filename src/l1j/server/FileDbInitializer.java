package l1j.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class FileDbInitializer {
    private static final Logger _log = Logger.getLogger(FileDbInitializer.class.getName());
    private static final String INIT_SQL_PATH = "./db/filedb/init.sql";
    private static final List<String> REQUIRED_TABLES = Arrays.asList(
            "accounts",
            "characters",
            "character_items",
            "castle",
            "mapids",
            "shop",
            "mobskill");
    private static final Pattern CREATE_TABLE_NAME =
            Pattern.compile("(?is)^\\s*CREATE\\s+TABLE\\s+`([^`]+)`\\s*\\(");

    private FileDbInitializer() {
    }

    public static void initializeIfNeeded() {
        if (!"filedb".equalsIgnoreCase(Config.DBMS)) {
            return;
        }
        if (!Config.FILE_DB_AUTO_INIT) {
            return;
        }
        try {
            File initScript = new File(INIT_SQL_PATH);
            if (!initScript.exists()) {
                _log.warning("FileDB init skipped: missing script " + INIT_SQL_PATH);
                return;
            }

            ensureDbDirectory();

            try (Connection con = L1DatabaseFactory.getInstance().getConnection()) {
                if (hasRequiredTables(con)) {
                    return;
                }

                executeInitScriptResilient(con, initScript);
                if (!hasRequiredTables(con)) {
                    throw new SQLException("FileDB init completed but required tables are still missing.");
                }
                _log.info("FileDB init complete using " + INIT_SQL_PATH + " (required tables verified)");
            }
        } catch (Exception e) {
            _log.log(Level.SEVERE, "Failed to initialize FileDB schema.", e);
            throw new Error("Failed to initialize FileDB schema.", e);
        }
    }

    private static void ensureDbDirectory() {
        File dbFile = resolveDbFile(Config.FILE_DB_PATH);
        File parent = dbFile.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
    }

    private static boolean hasRequiredTables(Connection con) {
        String sql = "SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES WHERE LOWER(TABLE_NAME)=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            for (String table : REQUIRED_TABLES) {
                ps.setString(1, table);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next() || rs.getInt(1) == 0) {
                        return false;
                    }
                }
            }
            return true;
        } catch (Exception e) {
            _log.log(Level.WARNING, "Failed to inspect existing FileDB tables.", e);
        }
        return false;
    }

    private static void executeInitScriptResilient(Connection con, File initScript) throws Exception {
        int executed = 0;
        int ignored = 0;
        StringBuilder statement = new StringBuilder();
        boolean inBlockComment = false;
        boolean inRoutineBlock = false;

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(initScript), StandardCharsets.UTF_8));
             Statement sql = con.createStatement()) {

            String line;
            while ((line = reader.readLine()) != null) {
                String trimmed = line.trim();
                if (trimmed.length() == 0) {
                    continue;
                }
                if (inRoutineBlock) {
                    if (isRoutineBlockEnd(trimmed)) {
                        inRoutineBlock = false;
                    }
                    continue;
                }
                if (inBlockComment) {
                    if (trimmed.contains("*/")) {
                        inBlockComment = false;
                    }
                    continue;
                }
                if (trimmed.startsWith("--")) {
                    continue;
                }
                if (trimmed.toUpperCase().startsWith("DELIMITER ")) {
                    continue;
                }
                if (isRoutineBlockStart(trimmed)) {
                    inRoutineBlock = true;
                    continue;
                }
                if (trimmed.startsWith("/*")) {
                    if (!trimmed.contains("*/")) {
                        inBlockComment = true;
                    }
                    continue;
                }
                if (trimmed.startsWith("*/") || trimmed.startsWith("*")) {
                    continue;
                }

                statement.append(line).append('\n');
                if (trimmed.endsWith(";")) {
                    String sqlText = statement.toString().trim();
                    statement.setLength(0);
                    if (sqlText.length() == 0) {
                        continue;
                    }
                    if (sqlText.endsWith(";")) {
                        sqlText = sqlText.substring(0, sqlText.length() - 1);
                    }
                    sqlText = normalizeStatement(sqlText);
                    try {
                        sql.execute(sqlText);
                        executed++;
                    } catch (SQLException e) {
                        if (isIgnorableInitError(e)) {
                            ignored++;
                            continue;
                        }
                        throw e;
                    }
                }
            }
        }
        _log.info("FileDB init script applied. executed=" + executed + ", ignored=" + ignored);
    }

    private static boolean isIgnorableInitError(SQLException e) {
        int code = e.getErrorCode();
        return code == 42101 || code == 23505 || code == 22001;
    }

    private static boolean isRoutineBlockStart(String trimmedLine) {
        String upper = trimmedLine.toUpperCase();
        return upper.startsWith("CREATE DEFINER=")
                || upper.startsWith("CREATE PROCEDURE")
                || upper.startsWith("CREATE FUNCTION")
                || upper.startsWith("CREATE TRIGGER");
    }

    private static boolean isRoutineBlockEnd(String trimmedLine) {
        String upper = trimmedLine.toUpperCase();
        return upper.equals("END")
                || upper.equals("END;")
                || upper.startsWith("END ")
                || upper.startsWith("END;");
    }

    private static String normalizeStatement(String sqlText) {
        String normalized = sqlText.replace('\u0000', ' ').trim();
        normalized = normalizeTrailingStorageFreeText(normalized);
        normalized = normalizeCreateTableIndexes(normalized);
        normalized = normalizeMysqlQuoteEscapes(normalized);
        return normalized;
    }

    private static String normalizeTrailingStorageFreeText(String sqlText) {
        String normalized = sqlText;
        if (normalized.toUpperCase().startsWith("CREATE TABLE")) {
            int end = normalized.indexOf(");");
            if (end >= 0) {
                normalized = normalized.substring(0, end + 2);
            }
        }
        normalized = normalized.replaceAll("(?i)\\s*(MyISAM|InnoDB)\\s+free:.*$", "");
        return normalized.trim();
    }

    private static String normalizeCreateTableIndexes(String sqlText) {
        Matcher tableMatcher = CREATE_TABLE_NAME.matcher(sqlText);
        if (!tableMatcher.find()) {
            return sqlText;
        }
        String tableName = tableMatcher.group(1);

        // H2는 index name을 schema 단위로 관리하므로, MySQL dump의 공통 key 이름(key_id 등)을
        // 테이블별 고유 이름으로 치환한다.
        return sqlText.replaceAll("(?i)(\\bKEY\\s+`)([^`]+)(`)", "$1" + tableName + "_$2$3");
    }

    private static String normalizeMysqlQuoteEscapes(String sqlText) {
        // MySQL dump의 백슬래시 단일인용 이스케이프(\')를 ANSI SQL 방식('')으로 변환한다.
        return sqlText.replace("\\'", "''");
    }

    private static File resolveDbFile(String configuredPath) {
        String path = configuredPath == null ? "" : configuredPath.trim();
        if (path.length() == 0) {
            path = "./data/filedb/l1jdb";
        }
        if (path.startsWith("jdbc:h2:file:")) {
            path = path.substring("jdbc:h2:file:".length());
            int optionIndex = path.indexOf(';');
            if (optionIndex >= 0) {
                path = path.substring(0, optionIndex);
            }
        }
        return new File(path + ".mv.db");
    }
}
