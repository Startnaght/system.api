package com.star.imgapi.serever.impl;

import com.star.imgapi.entity.Uploadteam;
import com.star.imgapi.serever.uploadserver;

public class uploadserverimpl implements uploadserver {
    //uploadteam 是传入的具体的参数

    @Override
    public void Saveimg(Uploadteam uploadteam) {
        String name = uploadteam.getName();
        String key = uploadteam.getKey();   //效验码 
        Object data = uploadteam.getData();  // 上传数据
        String type = uploadteam.getType();  // 上传类型
        System.out.println("保存图片"); 
    }

    @Override
    public void Savemp4() {
        System.out.println("保存视频");
    }
}