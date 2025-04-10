package com.star.imgapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

//@MapperScan("com.star.imgapi.mapper") // 扫描整个文件夹
@ComponentScan(basePackages = {
        "com.star.imgapi",
        "com.star.imgapi.controller",
        "com.star.imgapi.service",
        "com.star.imgapi.service.impl",
        "com.star.imgapi.config",
        "com.star.imgapi.util"
})
@SpringBootApplication()
public class EmailApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(EmailApiApplication.class, args);
        System.out.println("开始项目");
    }

}
