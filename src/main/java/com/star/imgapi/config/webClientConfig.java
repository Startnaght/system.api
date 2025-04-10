package com.star.imgapi.config;

import com.star.imgapi.entity.hitokotoCode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Configuration
public class webClientConfig {

    @Bean
    public WebClient webClient(WebClient.Builder builder) {
        return builder
                .baseUrl("https://v1.hitokoto.cn/")   //请求发送方地址
                .defaultHeader("Authorization", "Bearer your-token")  //请求发送方head
                .exchangeStrategies(
                        ExchangeStrategies.builder()
                        .codecs(configurer -> configurer
                                .defaultCodecs()
                                .maxInMemorySize(16*1024*1024))//设置请求最大限制16kb
                                .build())//
                .build();

    }

@Bean
    //使用拦截器记录日志文件
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder().
                filter(((request, next) -> {
                    System.out.println("Request: " + request.url());
                    return next.exchange(request);
                }));
    }

/**
 * @param
 * @param parm
 * @return
 * @author changan
 * @create
 **/
public Mono<hitokotoCode> fetchWithExceptionHandling(WebClient webClient, String parm) {
        return webClient.get()
                .uri(parm)
                .retrieve()
                .bodyToMono(hitokotoCode.class);   //指定返回格式
//                .doOnError(e ->{
//                    if (e instanceof WebClientRequestException) {
//                        WebClientResponseException webClientResponseException = (WebClientResponseException) e;
//                        GobalLog.error("NetWorkErro:"+e.getMessage());  //写入全局日志系统
//                        GobalLog.error("NetWorkWebclientRequestException:"+webClientResponseException.getResponseBodyAsString());
//                    }
//                });

}

}
