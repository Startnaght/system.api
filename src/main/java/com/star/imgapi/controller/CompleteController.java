package com.star.imgapi.controller;

import java.io.BufferedReader;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.star.imgapi.entity.complete;
import com.star.imgapi.serever.Mailservere;
import com.star.imgapi.util.GobalLog;
import com.star.imgapi.util.IpUtil;
import com.star.imgapi.util.R;
import com.star.imgapi.util.UtilOsStarem;
import com.star.imgapi.serever.impl.uploadserverimpl;
import java.util.UUID;

/**
 * @Author: oyster
 * @Description: 对外访问的接口
 * @DateTime: 2022/6/6 18:47
 **/
@RestController
@RequestMapping("/api")
public class CompleteController {
    @Autowired
    private Mailservere mailservere;

    /**
     * 暂时查出所有分类
     */
    @RequestMapping("/findall")
    public R list() {
        List<complete> listAll = mailservere.findAll();
        return R.ok().put("data", listAll);
    }

    @RequestMapping("/text")
    public R text() {
        return R.ok().put("明天", "依旧");
    }

    @PostMapping("/upload")
    public R upload(@RequestParam("file") MultipartFile data, @RequestParam("chunkIndex") String chunkIndex,
        @RequestParam("fileName") String fileName) {
        IpUtil ipadder = new IpUtil();
        String suffixName = fileName.substring(fileName.lastIndexOf("."));   // 获取文件后缀
        String prefixName = fileName.substring(0, fileName.lastIndexOf("."));   // 获取文件名称不包含后缀
        String uuid = UUID.randomUUID().toString().replace("-","");//创建服务器文件名称
        uploadserverimpl uploadserveri = new uploadserverimpl();  // 构建具体对象
        System.out.println("masses->接收到数据名称为：" + prefixName);   
        GobalLog.info("数据已经成功上传~来自长安:", ipadder.toString());
        // 图片编码为basee64 文本数据为utf-8
        if (suffixName.equals(".txt")) {   // 如果后缀是文本文件就执行下面
            uploadserveri.SaveTxt(data,(uuid+suffixName), chunkIndex);
        } else if (suffixName.equals(".jpg") || suffixName.equals(".png")) {
            uploadserveri.SaveImg(data, fileName);
        } else {
            return R.ok().put("message", "上传失败，服务器暂不支持此文件类型上传~");
        }

        return R.ok().put("message", "上传成功，存储名称为：" + (uuid+"."+suffixName));
    }
}
