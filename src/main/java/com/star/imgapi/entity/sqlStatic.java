package com.star.imgapi.entity;

import lombok.Data;

/**
 *
 *
 * @return  String sql
 * @author changan
 * @create  创建静态sql
 * @Date 2024.04.03
 **/
@Data
public class sqlStatic {
       String insertUserSql = "insert into user (id,uuid,name) values (?,?,?)";
       String insertYySql = "INSER INTO  responYy (id,uuid,hitokoto,type,from,from_who,creator,creator_uid,reviewer,commit_from,created_at,length) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
       String insertImgSql ="insert into imgandsql (id,uuid,name,img_path,save_date) values (?,?,?,?,?)";

}
