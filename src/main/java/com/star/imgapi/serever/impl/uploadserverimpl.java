package com.star.imgapi.serever.impl;

import com.star.imgapi.serever.uploadserver;
import com.star.imgapi.util.Code;
import com.star.imgapi.util.UtilOsStarem;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.io.File;
import com.star.imgapi.util.GobalLog;
@Service
public class uploadserverimpl implements uploadserver {
    //uploadteam 是传入的具体的参数

    @Override
    public void SaveTxt(MultipartFile data,String fileName,String chunkIndex) {
        UtilOsStarem utilOsStarem = new UtilOsStarem();  
        BufferedReader buffDate= utilOsStarem.readChunks(data,(Code.path+fileName));
        List<String> result=utilOsStarem.os_Save(buffDate, (Code.path+fileName)); //保存文件 数据类型为Object 保存路径为path
        if (buffDate == null) {
            GobalLog.error("errr:fileOutBuffreadNotErr:"+buffDate);
        }
        if (result.contains("error")) {
            GobalLog.info("error-> fileOutBufoutNotDuff:"); 
        }
    };

    @Override
    public void SaveImg (MultipartFile data,String fileName) {
        try {
            data.transferTo(new File(Code.path+fileName));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("上传失败");
        }

    }


    }
