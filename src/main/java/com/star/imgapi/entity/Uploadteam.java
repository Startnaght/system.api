package com.star.imgapi.entity;

// 上传所需要的参数 由前端传入

import lombok.Data;


@Data
public class Uploadteam {
    private String name; //文件名

    private String key;    //效验码

    private Object data;    // 上传数据

    private String type;    // 上传类型

    private String size;    // 上传大小
 
    private String time;    // 上传时间

    private Integer ipHome;   // 上传ip
}