package com.star.imgapi.entity;

import lombok.Data;

/**
 *
 *
 * @return  String sql
 * @author changan
 * @create  创建静态sql
 * @Date 2024.04.03
 **/

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SQL语句静态常量类
 * 统一管理所有数据库操作SQL语句
 *
 * @author changan
 * @create sqlStatic
 * @Date 2024.04.03
 **/
@Data
public class sqlStatic {

       private static final Logger logger = LoggerFactory.getLogger(sqlStatic.class);

       // ==================== 用户相关SQL ====================
       public static final String INSERT_USER_SQL = "INSERT INTO user (id, uuid, name) VALUES (?, ?, ?)";
       public static final String SELECT_USER_BY_ID = "SELECT * FROM user WHERE id = ?";
       public static final String UPDATE_USER_SQL = "UPDATE user SET name = ? WHERE id = ?";
       public static final String DELETE_USER_SQL = "DELETE FROM user WHERE id = ?";

       // ==================== 一言相关SQL ====================
       public static final String INSERT_YY_SQL = "INSERT INTO responYy (id, uuid, hitokoto, type, `from`, from_who, creator, creator_uid, reviewer, commit_from, created_at, length) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

       // 新的一言记录表SQL
       public static final String INSERT_HITOKOTO_RECORD_SQL =
               "INSERT INTO hitokoto_records (" +
                       "hitokoto_id, uuid, hitokoto_text, hitokoto_type, hitokoto_from, " +
                       "hitokoto_from_who, hitokoto_creator, hitokoto_creator_uid, " +
                       "hitokoto_reviewer, hitokoto_commit_from, hitokoto_created_at, " +
                       "hitokoto_length, api_category, request_ip, api_status" +
                       ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

       public static final String SELECT_HITOKOTO_BY_TYPE = "SELECT * FROM hitokoto_records WHERE hitokoto_type = ? ORDER BY request_time DESC LIMIT ?";
       public static final String SELECT_LATEST_HITOKOTO = "SELECT * FROM hitokoto_records ORDER BY request_time DESC LIMIT 1";
       public static final String COUNT_HITOKOTO_BY_CATEGORY = "SELECT hitokoto_type, COUNT(*) as count FROM hitokoto_records GROUP BY hitokoto_type";

       // ==================== 文件上传相关SQL ====================
       public static final String INSERT_IMG_SQL = "INSERT INTO imgandsql (id, uuid, name, img_path, save_date) VALUES (?, ?, ?, ?, ?)";

       public static final String INSERT_FILE_UPLOAD_RECORD_SQL =
               "INSERT INTO file_upload_records (" +
                       "file_uuid, original_name, stored_name, file_path, file_size, " +
                       "file_type, file_extension, upload_ip, upload_user, chunk_index, upload_status" +
                       ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

       public static final String SELECT_FILE_BY_UUID = "SELECT * FROM file_upload_records WHERE file_uuid = ?";
       public static final String SELECT_FILES_BY_TYPE = "SELECT * FROM file_upload_records WHERE file_type = ? ORDER BY upload_time DESC";
       public static final String UPDATE_FILE_DOWNLOAD_COUNT = "UPDATE file_upload_records SET download_count = download_count + 1, last_access_time = NOW() WHERE file_uuid = ?";
       public static final String DELETE_FILE_RECORD = "UPDATE file_upload_records SET upload_status = 'deleted' WHERE file_uuid = ?";

       // ==================== 操作日志相关SQL ====================
       public static final String INSERT_OPERATION_LOG_SQL =
               "INSERT INTO system_operation_logs (" +
                       "operation_type, operation_module, operation_description, " +
                       "request_method, request_url, client_ip" +
                       ") VALUES (?, ?, ?, ?, ?, ?)";

       public static final String SELECT_LOGS_BY_DATE = "SELECT * FROM system_operation_logs WHERE DATE(operation_time) = ? ORDER BY operation_time DESC";
       public static final String SELECT_LOGS_BY_TYPE = "SELECT * FROM system_operation_logs WHERE operation_type = ? ORDER BY operation_time DESC";

       // ==================== API统计相关SQL ====================
       public static final String INSERT_API_STATISTICS_SQL =
               "INSERT INTO api_access_statistics (" +
                       "api_path, api_method, access_date, access_hour, total_requests, " +
                       "success_requests, failed_requests, avg_response_time, unique_ips" +
                       ") VALUES (?, ?, CURDATE(), HOUR(NOW()), 1, ?, ?, ?, 1) " +
                       "ON DUPLICATE KEY UPDATE " +
                       "total_requests = total_requests + 1, " +
                       "success_requests = success_requests + ?, " +
                       "failed_requests = failed_requests + ?, " +
                       "avg_response_time = (avg_response_time * total_requests + ?) / (total_requests + 1)";

       public static final String SELECT_API_STATS_BY_DATE = "SELECT * FROM api_access_statistics WHERE access_date = ? ORDER BY api_path, access_hour";
       public static final String SELECT_POPULAR_APIS = "SELECT api_path, SUM(total_requests) as total FROM api_access_statistics GROUP BY api_path ORDER BY total DESC LIMIT 10";

       // ==================== 系统配置相关SQL ====================
       public static final String SELECT_SYSTEM_CONFIG = "SELECT config_value FROM system_configurations WHERE config_key = ?";
       public static final String UPDATE_SYSTEM_CONFIG = "UPDATE system_configurations SET config_value = ?, updated_time = NOW() WHERE config_key = ?";
       public static final String SELECT_ALL_CONFIGS = "SELECT config_key, config_value, config_description FROM system_configurations WHERE config_group = ?";

       // ==================== 数据统计SQL ====================
       public static final String GET_FILE_UPLOAD_STATS =
               "SELECT " +
                       "    DATE(upload_time) as upload_date, " +
                       "    file_type, " +
                       "    COUNT(*) as total_files, " +
                       "    SUM(file_size) as total_size, " +
                       "    AVG(file_size) as avg_size, " +
                       "    COUNT(DISTINCT upload_ip) as unique_ips " +
                       "FROM file_upload_records " +
                       "WHERE upload_status = 'completed' " +
                       "GROUP BY DATE(upload_time), file_type";

       public static final String GET_HITOKOTO_STATS =
               "SELECT " +
                       "    DATE(request_time) as request_date, " +
                       "    api_category, " +
                       "    COUNT(*) as total_requests, " +
                       "    SUM(CASE WHEN api_status = 'success' THEN 1 ELSE 0 END) as success_requests " +
                       "FROM hitokoto_records " +
                       "GROUP BY DATE(request_time), api_category";

       // ==================== 工具方法 ====================

       /**
        * 记录数据库连接关闭日志
        */
       public static void logConnectionClosed() {
              logger.info("数据库连接已关闭");
       }

       /**
        * 记录SQL执行日志
        */
       public static void logSqlExecution(String sql, Object... params) {
              if (logger.isDebugEnabled()) {
                     StringBuilder logMessage = new StringBuilder("执行SQL: ").append(sql);
                     if (params != null && params.length > 0) {
                            logMessage.append(" 参数: ");
                            for (int i = 0; i < params.length; i++) {
                                   logMessage.append(params[i]);
                                   if (i < params.length - 1) {
                                          logMessage.append(", ");
                                   }
                            }
                     }
                     logger.debug(logMessage.toString());
              }
       }

       /**
        * 记录SQL执行错误
        */
       public static void logSqlError(String sql, Exception e) {
              logger.error("SQL执行错误: {} - 错误信息: {}", sql, e.getMessage());
       }

       /**
        * 根据条件构建动态SQL（示例）
        */
       public static String buildFileQuerySql(String fileType, String uploadUser, String dateRange) {
              StringBuilder sql = new StringBuilder("SELECT * FROM file_upload_records WHERE 1=1");

              if (fileType != null && !fileType.isEmpty()) {
                     sql.append(" AND file_type = '").append(fileType).append("'");
              }

              if (uploadUser != null && !uploadUser.isEmpty()) {
                     sql.append(" AND upload_user = '").append(uploadUser).append("'");
              }

              if ("today".equals(dateRange)) {
                     sql.append(" AND DATE(upload_time) = CURDATE()");
              } else if ("week".equals(dateRange)) {
                     sql.append(" AND upload_time >= DATE_SUB(CURDATE(), INTERVAL 7 DAY)");
              } else if ("month".equals(dateRange)) {
                     sql.append(" AND upload_time >= DATE_SUB(CURDATE(), INTERVAL 30 DAY)");
              }

              sql.append(" ORDER BY upload_time DESC");
              return sql.toString();
       }
}