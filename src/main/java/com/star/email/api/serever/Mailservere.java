package com.star.email.api.serever;

import com.baomidou.mybatisplus.extension.service.IService;
import com.star.email.api.entity.complete;

import java.util.List;

/**
 * @Author: oyster
 * @Description: TODO
 * @DateTime: 2022/6/6 19:07
 **/

public interface Mailservere extends IService<complete> {

    List<complete> findAll();
}
