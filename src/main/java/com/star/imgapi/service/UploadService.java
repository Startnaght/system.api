package com.star.imgapi.service;

import com.star.imgapi.entity.UploadResult;
import org.apache.tomcat.jni.FileInfo;
import org.springframework.web.multipart.MultipartFile;

public interface UploadService {

    /**
     * 通用文件上传方法
     */
    UploadResult uploadFile(MultipartFile file, String fileName, String chunkIndex);

    /**
     * 保存图片文件
     */
    void saveImage(MultipartFile file, String fileName);

    /**
     * 保存文本文件
     */
    void saveText(MultipartFile file, String fileName, String chunkIndex);

    /**
     * 保存文档文件
     */
    void saveDocument(MultipartFile file, String fileName);

    /**
     * 保存视频文件
     */
    void saveVideo(MultipartFile file, String fileName);

    /**
     * 保存音频文件
     */
    void saveAudio(MultipartFile file, String fileName);

    /**
     * 保存压缩文件
     */
    void saveArchive(MultipartFile file, String fileName);

    /**
     * 保存其他类型文件
     */
    void saveOther(MultipartFile file, String fileName);

    /**
     * 删除文件
     */
    boolean deleteFile(String filePath);

    /**
     * 获取文件信息
     */
    FileInfo getFileInfo(String fileName);
}