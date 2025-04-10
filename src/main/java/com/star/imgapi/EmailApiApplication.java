package com.star.imgapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//@MapperScan("com.star.imgapi.mapper") // 扫描整个文件夹
@SpringBootApplication()
public class EmailApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(EmailApiApplication.class, args);
        System.out.println("开始项目");
    }

}
