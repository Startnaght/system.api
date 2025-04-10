package com.star.imgapi.util;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 文件类型工具类
 */
public class FileTypeUtil {

    // 文件类型分类映射
    private static final Map<String, String> FILE_CATEGORIES = Map.ofEntries(
            Map.entry("jpg", "image"), Map.entry("jpeg", "image"),
            Map.entry("png", "image"), Map.entry("gif", "image"),
            Map.entry("bmp", "image"), Map.entry("webp", "image"),
            Map.entry("txt", "document"), Map.entry("pdf", "document"),
            Map.entry("doc", "document"), Map.entry("docx", "document"),
            Map.entry("xls", "document"), Map.entry("xlsx", "document"),
            Map.entry("ppt", "document"), Map.entry("pptx", "document"),
            Map.entry("zip", "archive"), Map.entry("rar", "archive"),
            Map.entry("7z", "archive"), Map.entry("mp4", "video"),
            Map.entry("avi", "video"), Map.entry("mov", "video"),
            Map.entry("mkv", "video"), Map.entry("mp3", "audio"),
            Map.entry("wav", "audio"), Map.entry("flac", "audio")
    );

    // 允许的文件类型
    private static final List<String> ALLOWED_TYPES = Arrays.asList(
            "jpg", "jpeg", "png", "gif", "bmp", "webp", "txt", "pdf",
            "doc", "docx", "xls", "xlsx", "ppt", "pptx", "zip", "rar",
            "7z", "mp4", "avi", "mov", "mkv", "mp3", "wav", "flac"
    );

    /**
     * 获取文件分类
     */
    public static String getFileCategory(String fileExtension) {
        return FILE_CATEGORIES.getOrDefault(fileExtension.toLowerCase(), "other");
    }

    /**
     * 验证文件类型是否允许
     */
    public static boolean isAllowedType(String fileExtension) {
        return ALLOWED_TYPES.contains(fileExtension.toLowerCase());
    }

    /**
     * 获取所有支持的文件类型
     */
    public static List<String> getAllSupportedTypes() {
        return ALLOWED_TYPES;
    }

    /**
     * 按分类获取支持的文件类型
     */
    public static Map<String, List<String>> getSupportedTypesByCategory() {
        return ALLOWED_TYPES.stream()
                .collect(Collectors.groupingBy(FileTypeUtil::getFileCategory));
    }

    /**
     * 获取文件扩展名
     */
    public static String getFileExtension(String fileName) {
        if (fileName == null || fileName.lastIndexOf(".") == -1) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
    }

    /**
     * 验证文件大小是否在限制范围内
     */
    public static boolean isSizeWithinLimit(long fileSize, long maxSizeInMB) {
        long maxSizeInBytes = maxSizeInMB * 1024 * 1024;
        return fileSize <= maxSizeInBytes;
    }

    /**
     * 获取文件扩展名（带点）
     */
    public static String getFileExtensionWithDot(String fileName) {
        if (fileName == null || fileName.lastIndexOf(".") == -1) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf(".")).toLowerCase();
    }
}