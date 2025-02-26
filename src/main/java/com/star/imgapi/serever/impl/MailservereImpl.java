package com.star.imgapi.serever.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.star.imgapi.entity.complete;
import com.star.imgapi.mapper.CompleteMapper;
import com.star.imgapi.serever.Mailservere;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: oyster
 * @Description: TODO
 * @DateTime: 2022/5/22 21:04
 **/
@Service("mailservere")
public class MailservereImpl extends ServiceImpl<CompleteMapper, complete> implements Mailservere {

    @Override
    public List<complete> findAll() {
        // 获取全部数据
        List<complete> list = baseMapper.selectList(null);
        // list.stream ().filter ((data)->{
        //
        // })
        return list;
    }
}
