package com.star.imgapi.util;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
//实现基本so

public class UtilOsStarem {

    //创建基本的os数据操作工作类
    public List<String> os_Save(Object data, String path) { //data为操作数据，path为操作路径

        List< String> result = new ArrayList<>(); //创建返回结果
        FileWriter fileWriter = null; //创建文件写入对象
        try {
            fileWriter = new FileWriter(path); //创建文件写入对象
            fileWriter.write(data.toString()); //写入数据
            fileWriter.flush(); //刷新
            fileWriter.close(); //关闭
            result.add(ResultCode.success); //返回操作是否成功   
    }catch (Exception e) {
            e.printStackTrace(); //打印错误
            result.add(ResultCode.error);
            result.add(e.getMessage()); //返回操作是否成功
        }
     finally {
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result; // Add return statement
    }
}
