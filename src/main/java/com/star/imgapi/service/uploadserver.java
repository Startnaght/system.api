package com.star.imgapi.service;
import org.springframework.web.multipart.MultipartFile;
public interface  uploadserver{
     void SaveTxt(MultipartFile data,String fileName,String chunkIndex);
     void SaveImg (MultipartFile data,String fileName);
}