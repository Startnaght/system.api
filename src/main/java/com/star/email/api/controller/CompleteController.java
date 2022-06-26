package com.star.email.api.controller;

import com.star.email.api.entity.complete;
import com.star.email.api.serever.Mailservere;
import com.star.email.api.util.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author: oyster
 * @Description: TODO
 * @DateTime: 2022/6/6 18:47
 **/
@RestController
@RequestMapping("/mail")
public class CompleteController {

    @Autowired
    private Mailservere mailservere;

    /**
     * 暂时查出所有分类
     */
    @RequestMapping("/findall")
    public R list() {
        List<complete> listAll = mailservere.findAll ();

//        return R.ok ().put("data",listALL);
        return R.ok ().put ("data", listAll);
    }

    //@SuppressWarnings ()
    @RequestMapping("/text")
    public R text() {
        return R.ok ().put ("明天", "依旧");
    }


}
