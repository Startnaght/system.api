package com.star.imgapi.entity;

import lombok.Data;

/**
 * @Author: oyster
 * @Description:  这是实现 MybatisPlus 的s数据库操作 的一个字段表
 * @DateTime: 2022/6/6 18:16
 **/
@Data
public class complete {
    // 作者id
    private long id;

    // 作者信息
    private String name;

    // 图片地址
    private String imgUrl;

    // 分页地址
    private String pageye;

    // 插入时间
    private String insertData;

    // 更新时间
    private String upData;

}
