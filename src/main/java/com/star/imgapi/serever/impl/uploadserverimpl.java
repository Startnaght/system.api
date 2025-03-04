package com.star.imgapi.serever.impl;

import com.star.imgapi.entity.Uploadteam;
import com.star.imgapi.serever.uploadserver;
import com.star.imgapi.util.ResultCode;
import com.star.imgapi.util.UtilOsStarem;

public class uploadserverimpl implements uploadserver {
    //uploadteam 是传入的具体的参数

    @Override
    public void Saveimg(Uploadteam uploadteam) {
        UtilOsStarem utilOsStarem = new UtilOsStarem();  
        String name = uploadteam.getName(); //文件名
        String key = uploadteam.getKey();   //效验码 
        Object data = uploadteam.getData();  // 上传数据
        String type = uploadteam.getType();  // 上传类型
        utilOsStarem.os_Save(data, ResultCode.path); //保存文件 数据类型为Object 保存路径为path
        System.out.println("保存图片");
    }

    @Override
    public void Savemp4() {
        System.out.println("保存视频");
    }
}
