package com.star.imgapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * WebClient 配置类
 * 配置全局的 WebClient bean
 */
@Configuration
public class WebClientConfig {

    /**
     * 创建全局 WebClient bean
     */
    @Bean
    public WebClient webClient() {
        // 增加内存限制，避免大响应时出错
        ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(16 * 1024 * 1024)) // 16MB
                .build();

        return WebClient.builder()
                .baseUrl("https://v1.hitokoto.cn")
                .defaultHeader("User-Agent", "StarImageAPI/1.0.0")
                .exchangeStrategies(strategies)
                .build();
    }

    /**
     * 创建用于文件下载的 WebClient（更大的缓冲区）
     */
    @Bean(name = "fileDownloadWebClient")
    public WebClient fileDownloadWebClient() {
        ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(50 * 1024 * 1024)) // 50MB
                .build();

        return WebClient.builder()
                .exchangeStrategies(strategies)
                .build();
    }
}