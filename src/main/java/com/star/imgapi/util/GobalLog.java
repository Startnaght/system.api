package com.star.imgapi.util;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
//全局日志管理

public class GobalLog {
    private static final Logger logger = Logger.getLogger(GobalLog.class.getName());
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

    public static void info(String message,Integer ipHome) {
        message=message+"ip地址为："+ipHome;  //添加ip地址
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
}