package com.star.imgapi.service.impl;

import com.star.imgapi.entity.hitokotoCode;
import com.star.imgapi.service.HitokotoService;
import com.star.imgapi.util.GlobalLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * 修复的 HitokotoService 实现
 */
@Service
public  class HitokotoServiceImpl implements HitokotoService {

    private final WebClient webClient;

    /**
     * 通过构造函数注入 WebClient
     */
    @Autowired
    public HitokotoServiceImpl(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public Mono<hitokotoCode> getHitokoto(String category) {
        try {
            // 验证分类参数
            if (category == null || category.trim().isEmpty()) {
                category = "b"; // 默认分类
            }

            // 构建请求URL
            String url = "/?c=" + category + "&encode=json";
            System.out.println(url);

            GlobalLog.info("调用一言API，分类: " + category);

            // 使用注入的 WebClient
            return webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(hitokotoCode.class)
                    .doOnSuccess(result -> {
                        if (result != null) {
                            GlobalLog.info("一言API响应成功: " + result.getHitokoto());
                        }
                    })
                    .doOnError(error -> {
                        GlobalLog.error("一言API调用失败: " + error.getMessage());
                    });

        } catch (Exception e) {
            GlobalLog.error("获取一言异常: " + e.getMessage());
            return Mono.error(new RuntimeException("获取一言服务异常: " + e.getMessage()));
        }
    }

    @Override
    public Mono<hitokotoCode> getHitokotoWithFallback(String category) {
        return getHitokoto(category)
                .onErrorResume(e -> {
                    // 如果API调用失败，返回备用一言
                    GlobalLog.warn("使用备用一言，原因: " + e.getMessage());
                    return Mono.just(createFallbackHitokoto(category));
                });
    }

    /**
     * 创建备用一言
     */
    private hitokotoCode createFallbackHitokoto(String category) {
        hitokotoCode fallback = new hitokotoCode();
        fallback.setId(9999);
        fallback.setUuid("fallback-" + System.currentTimeMillis());
        fallback.setHitokoto("生活就像海洋，只有意志坚强的人才能到达彼岸。");
        fallback.setType(category != null ? category : "b");
        fallback.setFrom("系统备用");
        fallback.setFrom_who(null);
        fallback.setCreator("Star Image API");
        fallback.setCreator_uid("1");
        fallback.setReviewer(1);
        fallback.setCommit_from("web");
        fallback.setCreated_at((int) (System.currentTimeMillis() / 1000));
        fallback.setLength(20);

        return fallback;
    }
}