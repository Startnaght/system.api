package com.star.imgapi.util;

import com.star.imgapi.entity.sqlStatic;
import com.star.imgapi.entity.hitokotoCode;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.Properties;

/**
 * 使用sqlStatic的数据库工具类
 */
@Component
public class DatabaseUtil {
    private Connection connection;
    private static final String JDBC_URL = "jdbc:mariadb://127.0.0.1:3306/star_bigdata";
    private static final String USERNAME = "changan";
    private static final String PASSWORD = "102410";

    private static DatabaseUtil instance;

    private DatabaseUtil() {
        openDatabaseConnection();
    }

    public static synchronized DatabaseUtil getInstance() {
        if (instance == null) {
            instance = new DatabaseUtil();
        }
        return instance;
    }

    private void openDatabaseConnection() {
        try {
            Properties props = new Properties();
            props.setProperty("user", USERNAME);
            props.setProperty("password", PASSWORD);
            props.setProperty("useSSL", "false");
            props.setProperty("serverTimezone", "UTC");

            connection = DriverManager.getConnection(JDBC_URL, props);
            GlobalLog.info("star_bigdata 数据库连接成功");
        } catch (SQLException e) {
            GlobalLog.error("数据库连接失败: " + e.getMessage());
            throw new RuntimeException("star_bigdata 数据库连接失败", e);
        }
    }

    /**
     * 插入一言数据 - 使用sqlStatic中的常量
     */
    public void insertHitokotoData(hitokotoCode hitokoto, String requestIp, String apiCategory) {
        try (PreparedStatement ps = connection.prepareStatement(sqlStatic.INSERT_HITOKOTO_RECORD_SQL)) {

            // 记录SQL执行
            sqlStatic.logSqlExecution(sqlStatic.INSERT_HITOKOTO_RECORD_SQL,
                    hitokoto.getId(), hitokoto.getUuid(), hitokoto.getHitokoto(),
                    String.valueOf(hitokoto.getType()), hitokoto.getFrom(),
                    hitokoto.getFrom_who(), hitokoto.getCreator(),
                    hitokoto.getCreator_uid(), hitokoto.getReviewer(),
                    hitokoto.getCommit_from(), hitokoto.getCreated_at(),
                    hitokoto.getLength(), apiCategory, requestIp, "success");

            ps.setInt(1, hitokoto.getId());
            ps.setString(2, hitokoto.getUuid());
            ps.setString(3, hitokoto.getHitokoto());
            ps.setString(4, String.valueOf(hitokoto.getType()));
            ps.setString(5, hitokoto.getFrom());
            ps.setString(6, hitokoto.getFrom_who());
            ps.setString(7, hitokoto.getCreator());
            ps.setString(8, hitokoto.getCreator_uid());
            ps.setInt(9, hitokoto.getReviewer());
            ps.setString(10, hitokoto.getCommit_from());
            ps.setInt(11, hitokoto.getCreated_at());
            ps.setInt(12, hitokoto.getLength());
            ps.setString(13, apiCategory);
            ps.setString(14, requestIp);
            ps.setString(15, "success");

            ps.executeUpdate();
            GlobalLog.info("一言数据插入成功: " + hitokoto.getHitokoto());

        } catch (SQLException e) {
            sqlStatic.logSqlError(sqlStatic.INSERT_HITOKOTO_RECORD_SQL, e);
            insertErrorLog("insert_hitokoto", "插入一言数据失败: " + e.getMessage(), requestIp);
        }
    }

    /**
     * 插入文件上传记录 - 使用sqlStatic中的常量
     */
    public void insertFileUploadRecord(String originalName, String storedName, String filePath,
                                       long fileSize, String fileType, String uploadIp,
                                       String chunkIndex, String uploadUser) {
        try (PreparedStatement ps = connection.prepareStatement(sqlStatic.INSERT_FILE_UPLOAD_RECORD_SQL)) {

            String fileUuid = storedName.substring(0, storedName.lastIndexOf("."));
            String fileExtension = getFileExtension(originalName);

            sqlStatic.logSqlExecution(sqlStatic.INSERT_FILE_UPLOAD_RECORD_SQL,
                    fileUuid, originalName, storedName, filePath, fileSize,
                    fileType, fileExtension, uploadIp, uploadUser, chunkIndex, "completed");

            ps.setString(1, fileUuid);
            ps.setString(2, originalName);
            ps.setString(3, storedName);
            ps.setString(4, filePath);
            ps.setLong(5, fileSize);
            ps.setString(6, fileType);
            ps.setString(7, fileExtension);
            ps.setString(8, uploadIp);
            ps.setString(9, uploadUser);
            ps.setString(10, chunkIndex);
            ps.setString(11, "completed");

            ps.executeUpdate();
            GlobalLog.info("文件上传记录插入成功: " + originalName);

        } catch (SQLException e) {
            sqlStatic.logSqlError(sqlStatic.INSERT_FILE_UPLOAD_RECORD_SQL, e);
            insertErrorLog("insert_file", "文件记录插入失败: " + e.getMessage(), uploadIp);
        }
    }

    /**
     * 插入操作日志 - 使用sqlStatic中的常量
     */
    public void insertOperationLog(String operationType, String operationModule,
                                   String description, String requestMethod,
                                   String requestUrl, String clientIp) {
        try (PreparedStatement ps = connection.prepareStatement(sqlStatic.INSERT_OPERATION_LOG_SQL)) {

            sqlStatic.logSqlExecution(sqlStatic.INSERT_OPERATION_LOG_SQL,
                    operationType, operationModule, description, requestMethod, requestUrl, clientIp);

            ps.setString(1, operationType);
            ps.setString(2, operationModule);
            ps.setString(3, description);
            ps.setString(4, requestMethod);
            ps.setString(5, requestUrl);
            ps.setString(6, clientIp);

            ps.executeUpdate();

        } catch (SQLException e) {
            sqlStatic.logSqlError(sqlStatic.INSERT_OPERATION_LOG_SQL, e);
        }
    }

    /**
     * 更新API统计 - 使用sqlStatic中的常量
     */
    public void updateApiStatistics(String apiPath, String apiMethod, int responseTime,
                                    boolean success, String clientIp) {
        try (PreparedStatement ps = connection.prepareStatement(sqlStatic.INSERT_API_STATISTICS_SQL)) {

            int successFlag = success ? 1 : 0;
            int failFlag = success ? 0 : 1;

            sqlStatic.logSqlExecution(sqlStatic.INSERT_API_STATISTICS_SQL,
                    apiPath, apiMethod, successFlag, failFlag, responseTime,
                    successFlag, failFlag, responseTime);

            ps.setString(1, apiPath);
            ps.setString(2, apiMethod);
            ps.setInt(3, successFlag);
            ps.setInt(4, failFlag);
            ps.setInt(5, responseTime);
            ps.setInt(6, successFlag);
            ps.setInt(7, failFlag);
            ps.setInt(8, responseTime);

            ps.executeUpdate();

        } catch (SQLException e) {
            sqlStatic.logSqlError(sqlStatic.INSERT_API_STATISTICS_SQL, e);
        }
    }

    /**
     * 获取系统配置 - 使用sqlStatic中的常量
     */
    public String getSystemConfig(String configKey) {
        try (PreparedStatement ps = connection.prepareStatement(sqlStatic.SELECT_SYSTEM_CONFIG)) {

            sqlStatic.logSqlExecution(sqlStatic.SELECT_SYSTEM_CONFIG, configKey);
            ps.setString(1, configKey);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("config_value");
            }
        } catch (SQLException e) {
            sqlStatic.logSqlError(sqlStatic.SELECT_SYSTEM_CONFIG, e);
        }

        return null;
    }

    /**
     * 获取文件上传统计 - 使用sqlStatic中的常量
     */
    public ResultSet getFileUploadStats() throws SQLException {
        PreparedStatement ps = connection.prepareStatement(sqlStatic.GET_FILE_UPLOAD_STATS);
        sqlStatic.logSqlExecution(sqlStatic.GET_FILE_UPLOAD_STATS);
        return ps.executeQuery();
    }

    /**
     * 获取一言统计 - 使用sqlStatic中的常量
     */
    public ResultSet getHitokotoStats() throws SQLException {
        PreparedStatement ps = connection.prepareStatement(sqlStatic.GET_HITOKOTO_STATS);
        sqlStatic.logSqlExecution(sqlStatic.GET_HITOKOTO_STATS);
        return ps.executeQuery();
    }

    // 其他辅助方法保持不变...
    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
    }

    private void insertErrorLog(String errorType, String errorMessage, String clientIp) {
        try (PreparedStatement ps = connection.prepareStatement(sqlStatic.INSERT_OPERATION_LOG_SQL)) {
            ps.setString(1, "error");
            ps.setString(2, "system");
            ps.setString(3, errorType + ": " + errorMessage);
            ps.setString(4, "POST");
            ps.setString(5, "/api/error");
            ps.setString(6, clientIp);
            ps.executeUpdate();
        } catch (SQLException e) {
            GlobalLog.error("错误日志插入失败: " + e.getMessage());
        }
    }

    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                sqlStatic.logConnectionClosed(); // 使用sqlStatic的日志方法
            } catch (SQLException e) {
                GlobalLog.error("关闭数据库连接失败: " + e.getMessage());
            }
        }
    }
}