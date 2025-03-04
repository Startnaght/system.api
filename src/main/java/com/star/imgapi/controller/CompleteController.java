package com.star.imgapi.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.star.imgapi.entity.Uploadteam;
import com.star.imgapi.entity.complete;
import com.star.imgapi.serever.Mailservere;
import com.star.imgapi.serever.impl.uploadserverimpl;
import com.star.imgapi.util.R;

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

    @PostMapping("/upload") //post请求
    public R upload(@RequestBody Uploadteam uploadteam) {  //接收前端传来的数据
        uploadserverimpl uploadserverimpl = new uploadserverimpl(); //实例化
        uploadserverimpl.Saveimg(uploadteam); //调用保存图片的方法

        return R.ok().put(("上传成功，存储路径为："),200);
    }

}
