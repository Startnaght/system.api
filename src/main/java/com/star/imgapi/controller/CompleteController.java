package com.star.imgapi.controller;

import java.util.List;

import com.star.imgapi.serever.impl.uploadserverimpl;
import com.star.imgapi.util.GobalLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.star.imgapi.entity.complete;
import com.star.imgapi.serever.Mailservere;
import com.star.imgapi.util.R;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import com.star.imgapi.entity.Uploadteam;
import com.star.imgapi.util.IpUtil;

/**
 * @Author: oyster
 * @Description: 对外访问的接口
 * @DateTime: 2022/6/6 18:47
 **/
@RestController
@RequestMapping("/api")
public class CompleteController {
//访问路径ip:/main/..   
    @Autowired
    private Mailservere mailservere;

    /**
     * 暂时查出所有分类
     */
    @RequestMapping("/findall")
    public R list() {
        List<complete> listAll = mailservere.findAll();

        // return R.ok ().put("data",listALL);
        return R.ok().put("data", listAll);
    }

    // @SuppressWarnings ()
    @RequestMapping("/text")
    public R text() {
        return R.ok().put("明天", "依旧");
    }

    // RequestBody 只能用于json的数据的接受  包括表单数据
    @PostMapping("/upload") //post请求 Uploadteam uploadteam)
    public R upload(
        @RequestParam("file") MultipartFile data,
        @RequestParam("chunkIndex") int chunkIndex,
        @RequestParam("fileName") String fileName
    ) {  //接收前端传来的数据
        IpUtil ipadder = new IpUtil();
        System.out.println("接收到的数据: " +fileName);
        GobalLog.info("数据已经成功上传~来自长安:",ipadder.toString());
        uploadserverimpl uploadserverimpl = new uploadserverimpl(); //实例化
        uploadserverimpl.Saveimg(data); //调用保存图片的方法

        return R.ok().put("上传成功，存储路径为：", 200);
    }
    
    // public Integer request() {
    //     return new Integer();
    // }

}
