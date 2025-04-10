package com.star.imgapi.util;
import lombok.Data;

@Data
public class ResponNetwork<T> {
    private T data;

    /**
 * @Author: oyster
 * @Description: 返回get请求成功
 * @DateTime: 2022/5/22 21:14
 *
     * @return*/
    public ResponNetwork(T  data){

        this.data= data;
    }

    public ResponNetwork(T data, String message){

         this.data= data;
    }

}
