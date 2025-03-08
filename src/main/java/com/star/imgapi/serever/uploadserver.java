package com.star.imgapi.serever;
import com.star.imgapi.entity.Uploadteam;
import org.springframework.web.multipart.MultipartFile;
public interface  uploadserver{
    public void Saveimg(MultipartFile data);
    public void Savemp4 ();
}