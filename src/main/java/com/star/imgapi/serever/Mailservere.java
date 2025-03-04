package com.star.imgapi.serever;

import com.baomidou.mybatisplus.extension.service.IService;
import com.star.imgapi.entity.complete;

import java.util.List;
// 接口大概实现
/**
 * @Author: oyster
 * @Description: TODO
 * @DateTime: 2022/6/6 19:07
 **/

public interface Mailservere extends IService<complete> {

    List<complete> findAll();
}
