package com.star.imgapi.serever;
import com.star.imgapi.entity.Uploadteam;
import org.springframework.web.multipart.MultipartFile;
public interface  uploadserver{
    public void SaveTxt(MultipartFile data,String fileName,String chunkIndex);
    public void SaveImg (MultipartFile data,String fileName);
}