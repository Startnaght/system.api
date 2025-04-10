package com.star.imgapi.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 响应结构构建工具 - 修复版
 */
public class ReStruct {

    public Map<String, Object> ReJsonSuccess(List<String> messages) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("code", 200);
        result.put("message", "操作成功");
        result.put("data", messages);
        result.put("timestamp", System.currentTimeMillis());
        return result;
    }

    public Map<String, Object> ReJsonErr(List<String> messages) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        result.put("code", 400);
        result.put("message", "操作失败");
        result.put("error", messages);
        result.put("timestamp", System.currentTimeMillis());
        return result;
    }

    public Map<String, Object> ReJsonData(Object data, String message) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("code", 200);
        result.put("message", message);
        result.put("data", data);
        result.put("timestamp", System.currentTimeMillis());
        return result;
    }

}