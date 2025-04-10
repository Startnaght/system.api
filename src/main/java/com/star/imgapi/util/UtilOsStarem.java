package com.star.imgapi.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;

@Component
public class UtilOsStarem {

    // 合并数据
    public BufferedReader readChunks(MultipartFile data, String path) {
        BufferedReader reader = null;
        System.out.println("开始合并分片数据");
        try {
            reader = new BufferedReader(new InputStreamReader(data.getInputStream(),"utf-8"));
        } catch (Exception e) {
            GlobalLog.error(e.getMessage());
        }
        return reader;
    }

    public List<String> os_Save(BufferedReader data, String path) {
        BufferedWriter isCloneStuot = null;
        System.out.println("path:" + path);
        // File pathDir = new File(path);
        // if (!pathDir.exists()) {
        //     pathDir.mkdirs();
        // }

        List<String> result = new ArrayList<>();
        try (BufferedWriter buffWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path),"Utf-8") )) {
            char[] buffer = new char[1024];
            int bytesRead;
            while ((bytesRead = data.read(buffer)) != -1) {
                buffWriter.write(buffer, 0, bytesRead);
            }
            buffWriter.close();
            isCloneStuot = buffWriter;
        } catch (Exception e) {
            e.printStackTrace();
            result.add("error");
            result.add(e.getMessage());
        } finally {
            if (isCloneStuot != null) {
                try {
                    isCloneStuot.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    result.add(e.getMessage());
                    GlobalLog.debug(e.getMessage());
                }
            }
        }
        return result;
    }
}
