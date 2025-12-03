// java
package com.star.imgapi.config;

import com.star.imgapi.entity.hitokotoCode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import com.star.imgapi.util.GlobalLog;

@Configuration
public class webClientConfig {

    @Bean
    public WebClient webClient(WebClient.Builder builder) {
        return builder
                .baseUrl("https://v1.hitokoto.cn/")   // 请求发送方地址
                .defaultHeader("Authorization", "Bearer your-token")  // 请求发送方 header
                .exchangeStrategies(
                        ExchangeStrategies.builder()
                                .codecs(configurer -> configurer
                                        .defaultCodecs()
                                        .maxInMemorySize(16 * 1024 * 1024)) // 设置请求最大限制 16MB
                                .build())
                .build();
    }

    @Bean
    // 使用拦截器记录日志文件
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder()
                .filter((request, next) -> {
                    System.out.println("Request: " + request.url());
                    return next.exchange(request);
                });
    }

    /**
     * @param webClient
     * @param parm
     * @return
     */
    public Mono<hitokotoCode> fetchWithExceptionHandling(WebClient webClient, String parm) {
        return webClient.get()
                .uri(parm)
                .retrieve()
                .bodyToMono(hitokotoCode.class)   // 指定返回格式
                .doOnError(e -> {
                    if (e instanceof WebClientRequestException) {
                        WebClientRequestException reqEx = (WebClientRequestException) e;
                        GlobalLog.error("WebClientRequestException: " + reqEx.getMessage());
                    } else if (e instanceof WebClientResponseException) {
                        WebClientResponseException respEx = (WebClientResponseException) e;
                        GlobalLog.error("WebClientResponseException: " + respEx.getStatusCode()
                                + " body: " + respEx.getResponseBodyAsString());
                    } else {
                        GlobalLog.error("UnknownError: " + e.getMessage());
                    }
                });
    }

}