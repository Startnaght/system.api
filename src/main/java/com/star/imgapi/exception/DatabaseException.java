package com.star.imgapi.exception;


import com.star.imgapi.util.Code;
import lombok.Getter;

/**
 * 数据库操作异常
 */
@Getter
public class DatabaseException extends BusinessException {
    private final String sql;
    private final String operation;

    public DatabaseException(Code errorCode, String message, String sql, String operation) {
        super(errorCode, message);
        this.sql = sql;
        this.operation = operation;
    }

    public DatabaseException(Code errorCode, String message, String sql, String operation, Throwable cause) {
        super(errorCode, message, cause);
        this.sql = sql;
        this.operation = operation;
    }
}