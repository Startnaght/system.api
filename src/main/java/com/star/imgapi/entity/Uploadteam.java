package com.star.imgapi.entity;

// 上传所需要的参数 由前端传入

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
import javax.swing.event.InternalFrameEvent;


@Data
public class Uploadteam {  
    
    private String name; //文件名
   
    private Integer index; 

    // private String key;    //效验码

    // private String type;    // 上传类型

    // private String size;    // 上传大小
 
    // private String time;    // 上传时间

    // private Integer ipHome;   // 上传ip
}