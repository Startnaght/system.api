package com.star.imgapi.service;

import com.star.imgapi.entity.hitokotoCode;
import reactor.core.publisher.Mono;

/**
 * Hitokoto服务接口
 */
public interface HitokotoService {

    /**
     * 获取一言
     */
    Mono<hitokotoCode> getHitokoto(String category);

    /**
     * 获取一言（带备用方案）
     */
    Mono<hitokotoCode> getHitokotoWithFallback(String category);
}