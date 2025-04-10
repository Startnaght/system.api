package com.star.imgapi.util;

import java.io.IOException;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
//全局日志管理

public class GlobalLog {
    private static final Logger logger = Logger.getLogger(GlobalLog.class.getName());
    private static FileHandler fileHandler;

    static {
        try {
            // Configure the logger with handler and formatter
            fileHandler = new FileHandler("application.log", true);
            logger.addHandler(fileHandler);
            SimpleFormatter formatter = new SimpleFormatter();
            fileHandler.setFormatter(formatter);
            logger.setLevel(Level.ALL);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to initialize logger handler.", e);
        }
    }

    public static void info(String message) {
        logger.log(Level.INFO, message);
    }

    public static void warning(String message) {
        logger.log(Level.WARNING, message);
    }

    public static void error(String message) {
        logger.log(Level.SEVERE, message);
    }

    public static void debug(String message) {
        logger.log(Level.FINE, message);
    }

    public static void error(String s, Code errorCode, String message, String requestURI) {
    }

    public static void error(String s, Map<String, String> errors) {

    }

    public static void error(String s, String message, String requestURI) {
    }

    //暂时没写
    public static void warn(String s) {

    }

    public static void error(String s, String message) {
    }
}