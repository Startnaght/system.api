package com.star.imgapi.util;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


import java.util.HashMap;
import java.util.Map;

/**
 * @Author: oyster
 * @Description: TODO
 * @DateTime: 2022/5/22 21:14
 **/
// 统一结果返回类
@Data // data自动添加get、set 方法
public class R extends HashMap<String, Object> {
    @ApiModelProperty(value = "是否成功")
    private Boolean success;

    @ApiModelProperty(value = "返回码")
    private Integer code;

    @ApiModelProperty(value = "返回消息")
    private String message;

    @ApiModelProperty(value = "返回数据")
    private Map<String, Object> data = new HashMap<String, Object>();

    // 把构造方法私有
    private R() {
    }


    // 成功静态方法
    public static R ok() {
        R r = new R();
        r.setSuccess(true);
        r.setCode(ResultCode.SUCCESS);
        r.setMessage("成功");
        return r;
    }

    public static R error() {
        R r = new R();
        r.setSuccess(false);
        r.setCode(ResultCode.ERROR);
        r.setMessage("失败");
        return r;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }
    public void setMessage(String message) {
        this.message = message;
    }

    public R success(Boolean success) {
        this.setSuccess(success);
        return this;
    }

    public R message(Integer code) {
        this.setCode(code);
        return this;
    }

    public R code(Integer code) {
        this.setCode(code);
        return this;
    }

    // 以hash的方式返回
    public R put(String key, Object value) {
        super.put(key, value);
        return this;
    }

    // public R json()
}
