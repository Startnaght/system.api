package com.star.imgapi.controller;

import java.util.ArrayList;
import java.util.List;

import com.star.imgapi.config.webClientConfig;
import com.star.imgapi.entity.Uploadteam;
import com.star.imgapi.entity.hitokotoCode;
import com.star.imgapi.entity.sqlStatic;
import com.star.imgapi.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.star.imgapi.serever.impl.uploadserverimpl;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

/**
 * @Author: oyster
 * @Description: 对外访问的接口
 * @DateTime: 2022/6/6 18:47
 **/
@RestController  //返回json数据
@RequestMapping("/api")
public class CompleteController {
    private WebClient.Builder webClientBuilder;
    @Autowired
    private HttpServletRequest request;
    // /**
    //  * 暂时查出所有分类
    //  */
    // @RequestMapping("/findall")
    // public R list() {
    //     List<complete> listAll = mailservere.findAll();
    //     return R.ok().put("data", listAll);
    // }

    @PostMapping("/text")
    public ResponNetwork<Code> text() {
        return new ResponNetwork<Code>(Code.UNAUTHORIZED);
    }
/**
 *
 *
 * @param uploadteam
 * @return 需要返回一言的详细接口数据 json  暂时返回sunccess 成功
 * @author changan
 * @create 创建服务器对接口的支持或者支撑 strPram创建为带传入参数
 **/
    @Autowired
    private webClientConfig webClient;
@PostMapping("/yiyan")
    public ResponNetwork<Code> NetworkYi(@RequestBody Uploadteam uploadteam) {
        String requestsName=uploadteam.getName();
        Integer responesIndex = uploadteam.getIndex();
        String strPram="?c=b";//requestsName+responesIndex.toString()
        Mono<hitokotoCode>  responyy = webClient.fetchWithExceptionHandling(
                webClient.webClient(webClientBuilder)
                ,strPram);
//保存请求
    System.out.printf("一言接口返回数据："+responyy.block());
    new openDabases().insertData(new sqlStatic().getInsertYySql(),0,responyy.block());
    return new ResponNetwork<>(Code.SUCCESS);
    }

    @Autowired
    private uploadserverimpl  uploadserveri;
    @PostMapping("/upload")
    public ResponNetwork upload(@RequestParam("file") MultipartFile data, @RequestParam("chunkIndex") String chunkIndex,
                                @RequestParam("fileName") String fileName) {
        String suffixName = fileName.substring(fileName.lastIndexOf("."));   // 获取文件后缀
//        String prefixName = fileName.substring(0, fileName.lastIndexOf("."));   // 获取文件名称不包含后缀
        String uuid = UUID.randomUUID().toString().replace("-","");//创建服务器文件名称
//        uploadserverimpl uploadserveri = new uploadserverimpl();  // 构建具体对象
        if (suffixName.equals(".txt")) {   // 如果后缀是文本文件就执行下面
            uploadserveri.SaveTxt(data,(uuid+suffixName), chunkIndex);
        } else if (suffixName.equals(".jpg") || suffixName.equals(".png")) {
            uploadserveri.SaveImg(data, (uuid+suffixName));
        } else {
            return new ResponNetwork<>(new ReStruct().ReJsonErr(new ArrayList<String>(
                    List.of("失败"))));//"上传失败，服务器暂不支持此文件类型上传~"
        }
        return new ResponNetwork<>(new ReStruct().ReJsonSuccess(new ArrayList<String>(
                List.of("长安管理平台开发者-->> 欢迎你的使用~~",
                        (uuid+suffixName),
                        fileName,
                        request.getRequestURI()
                ))));
    }
}
