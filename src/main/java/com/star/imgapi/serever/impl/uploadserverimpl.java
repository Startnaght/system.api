package com.star.imgapi.serever.impl;

import com.star.imgapi.entity.Uploadteam;
import com.star.imgapi.serever.uploadserver;
import com.star.imgapi.util.ResultCode;
import com.star.imgapi.util.UtilOsStarem;
import org.springframework.web.multipart.MultipartFile;

public class uploadserverimpl implements uploadserver {
    //uploadteam 是传入的具体的参数

    @Override
    public void Saveimg(MultipartFile data) {
        UtilOsStarem utilOsStarem = new UtilOsStarem();  
        utilOsStarem.os_Save(data, ResultCode.path); //保存文件 数据类型为Object 保存路径为path
        System.out.println("保存图片");
    }

    // @Override
    // public void Saveimg(Uploadteam uploadteam) {

    // }

    @Override
    public void Savemp4() {
        System.out.println("保存视频");
    }
}
