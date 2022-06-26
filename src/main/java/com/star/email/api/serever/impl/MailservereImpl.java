package com.star.email.api.serever.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.star.email.api.entity.complete;
import com.star.email.api.mapper.CompleteMapper;
import com.star.email.api.serever.Mailservere;
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
//        获取全部数据
        List<complete> list = baseMapper.selectList (null);
//        list.stream ().filter ((data)->{
//
//        })
        return list;
    }
}
