package com.star.imgapi.factory;

import com.star.imgapi.strategy.FileProcessStrategy;
import com.star.imgapi.util.GlobalLog;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 修复后的文件处理器工厂
 */
@Component
public class FileProcessorFactory implements ApplicationContextAware {

    private ApplicationContext applicationContext;
    private List<FileProcessStrategy> strategies;
    private Map<String, FileProcessStrategy> strategyMap;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @PostConstruct
    public void init() {
        // 方式1：使用ApplicationContext获取所有策略bean
        Map<String, FileProcessStrategy> beans = applicationContext.getBeansOfType(FileProcessStrategy.class);
        this.strategies = new ArrayList<>(beans.values());
        this.strategyMap = beans;

        GlobalLog.info("文件处理器工厂初始化完成，找到 " + strategies.size() + " 个处理器:");
        strategies.forEach(strategy ->
                GlobalLog.info(" - " + strategy.getClass().getSimpleName()));
    }

    /**
     * 根据文件扩展名获取处理器
     */
    public FileProcessStrategy getProcessor(String fileExtension) {
        if (strategies == null || strategies.isEmpty()) {
            GlobalLog.error("没有找到可用的文件处理器");
            return null;
        }

        for (FileProcessStrategy strategy : strategies) {
            if (strategy.supports(fileExtension)) {
                return strategy;
            }
        }

        GlobalLog.warning("找不到支持 " + fileExtension + " 格式的文件处理器");
        return null;
    }

    /**
     * 获取所有处理器
     */
    public List<FileProcessStrategy> getAllProcessors() {
        return new ArrayList<>(strategies);
    }

    /**
     * 注册处理器
     */
    public void registerProcessor(String fileType, FileProcessStrategy strategy) {
        if (strategyMap != null) {
            strategyMap.put(fileType, strategy);
        }
        if (strategies != null && !strategies.contains(strategy)) {
            strategies.add(strategy);
        }
    }
}