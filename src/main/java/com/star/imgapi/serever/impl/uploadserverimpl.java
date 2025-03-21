package com.star.imgapi.serever.impl;

import com.star.imgapi.entity.Uploadteam;
import com.star.imgapi.serever.uploadserver;
import com.star.imgapi.util.R;
import com.star.imgapi.util.ResultCode;
import com.star.imgapi.util.UtilOsStarem;
import org.springframework.web.multipart.MultipartFile;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.File;

public class uploadserverimpl implements uploadserver {
    //uploadteam 是传入的具体的参数

    @Override
    public void SaveTxt(MultipartFile data,String fileName,String chunkIndex) {
        UtilOsStarem utilOsStarem = new UtilOsStarem();  
        BufferedReader buffDate= utilOsStarem.readChunks(data,(ResultCode.path+fileName));
        List<String> result=utilOsStarem.os_Save(buffDate, (ResultCode.path+fileName)); //保存文件 数据类型为Object 保存路径为path
        if (buffDate == null) {
             R.error().put("message", "读取分片数据失败");
        }
        if (result.contains("error")) {
             R.error().put("message", "上传失败");
        }
    };

    @Override
    public void SaveImg (MultipartFile data,String fileName) {
        try {
            data.transferTo(new File(ResultCode.path+fileName));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("上传失败");
        }

    }


    }
