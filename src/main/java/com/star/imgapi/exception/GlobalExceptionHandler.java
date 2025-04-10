package com.star.imgapi.exception;

import com.star.imgapi.util.Code;
import com.star.imgapi.util.GlobalLog;
import com.star.imgapi.util.ResponseWrapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 修复的全局异常处理器
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理请求体格式错误异常
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseWrapper<Map<String, Object>> handleHttpMessageNotReadable(
            HttpMessageNotReadableException e, HttpServletRequest request) {

        GlobalLog.error("请求体格式错误: {}, 路径: {}", e.getMessage(), request.getRequestURI());

        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("errorCode", Code.HTTP_MESSAGE_NOT_READABLE.name());
        errorDetails.put("codeValue", Code.HTTP_MESSAGE_NOT_READABLE.getCode());
        errorDetails.put("timestamp", LocalDateTime.now());
        errorDetails.put("path", request.getRequestURI());
        errorDetails.put("message", "请求体格式错误，请检查JSON格式");
        errorDetails.put("suggestion", "请确保请求体是有效的JSON格式，且包含所有必需字段");

        return ResponseWrapper.error(Code.HTTP_MESSAGE_NOT_READABLE.getMessage(), errorDetails.toString());
    }

    /**
     * 处理参数验证异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseWrapper<Map<String, Object>> handleValidationExceptions(
            MethodArgumentNotValidException e) {

        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        GlobalLog.error("参数验证失败: {}", errors);

        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("errorCode", Code.METHOD_ARGUMENT_NOT_VALID.name());
        errorDetails.put("codeValue", Code.METHOD_ARGUMENT_NOT_VALID.getCode());
        errorDetails.put("fieldErrors", errors);
        errorDetails.put("message", "参数验证失败");

        return ResponseWrapper.error("参数验证失败", errorDetails.toString());
    }

    /**
     * 处理约束违反异常
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseWrapper<Map<String, Object>> handleConstraintViolationException(
            ConstraintViolationException e) {

        Map<String, String> errors = new HashMap<>();
        e.getConstraintViolations().forEach(violation -> {
            String fieldName = extractFieldName(violation.getPropertyPath().toString());
            String errorMessage = violation.getMessage();
            errors.put(fieldName, errorMessage);
        });

        GlobalLog.error("约束违反异常: {}", errors);

        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("errorCode", Code.CONSTRAINT_VIOLATION.name());
        errorDetails.put("codeValue", Code.CONSTRAINT_VIOLATION.getCode());
        errorDetails.put("fieldErrors", errors);
        errorDetails.put("message", "参数约束验证失败");

        return ResponseWrapper.error("参数约束验证失败", errorDetails.toString());
    }

    /**
     * 处理缺少请求参数异常
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseWrapper<Map<String, Object>> handleMissingServletRequestParameter(
            MissingServletRequestParameterException e) {

        GlobalLog.error("缺少请求参数: {}", e.getMessage());

        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("errorCode", Code.MISSING_SERVLET_REQUEST_PARAMETER.name());
        errorDetails.put("codeValue", Code.MISSING_SERVLET_REQUEST_PARAMETER.getCode());
        errorDetails.put("parameterName", e.getParameterName());
        errorDetails.put("parameterType", e.getParameterType());
        errorDetails.put("message", "缺少必需参数: " + e.getParameterName());

        return ResponseWrapper.error("缺少必需参数: " + e.getParameterName(), errorDetails.toString());
    }

    /**
     * 处理参数类型不匹配异常
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseWrapper<Map<String, Object>> handleTypeMismatch(
            MethodArgumentTypeMismatchException e) {

        GlobalLog.error("参数类型不匹配: {}", e.getMessage());

        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("errorCode", Code.TYPE_MISMATCH.name());
        errorDetails.put("codeValue", Code.TYPE_MISMATCH.getCode());
        errorDetails.put("parameterName", e.getName());
        errorDetails.put("requiredType", e.getRequiredType() != null ? e.getRequiredType().getSimpleName() : "未知");
        errorDetails.put("actualValue", e.getValue());
        errorDetails.put("message", "参数类型不匹配: " + e.getName());

        return ResponseWrapper.error("参数类型不匹配: " + e.getName(), errorDetails.toString());
    }

    /**
     * 修复的全局异常处理 - 避免枚举转换错误
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseWrapper<Map<String, Object>> handleGlobalException(
            Exception e, HttpServletRequest request) {

        // 使用安全的方式获取错误码
        Code errorCode = Code.fromException(e.getClass());

        GlobalLog.error("系统异常 - 类型: {}, 消息: {}, 路径: {}",
                Code.valueOf(e.getClass().getSimpleName()), e.getMessage(), request.getRequestURI());

        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("errorCode", errorCode.name());
        errorDetails.put("codeValue", errorCode.getCode());
        errorDetails.put("errorType", e.getClass().getSimpleName());
        errorDetails.put("timestamp", LocalDateTime.now());
        errorDetails.put("path", request.getRequestURI());

        // 生产环境隐藏详细错误信息
        boolean isProd = true; // 实际应该从配置读取
        if (!isProd) {
            errorDetails.put("detail", e.getMessage());
        } else {
            errorDetails.put("detail", "系统繁忙，请稍后重试");
        }

        return ResponseWrapper.error(errorCode.getMessage(), errorDetails.toString());
    }

    /**
     * 提取字段名
     */
    private String extractFieldName(String propertyPath) {
        if (propertyPath.contains(".")) {
            return propertyPath.substring(propertyPath.lastIndexOf(".") + 1);
        }
        return propertyPath;
    }
}