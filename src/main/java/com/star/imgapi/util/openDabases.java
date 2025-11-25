package com.star.imgapi.util;

import com.star.imgapi.entity.hitokotoCode;
import com.star.imgapi.entity.sqlStatic;

import java.sql.*;
import java.util.List;

/**
 *
 *
 * @param
 * @return
 * @author changan
 * @create  创建数据库接口  创建删除查询  都通过此子类开始
 **/
public class openDabases {
    private static Connection connection;   //创建链接的数据
    private static String JDBC ="jdbc:mariadb://192.168.0.100:3306/changanPlatform" ;
    public  openDabases() {
            openDatabaesCoonection();


    }
//打开数据接口
    private void openDatabaesCoonection()  {
        System.out.printf("openDatabaesCoonection开始连接\n" +JDBC);

        try {
            connection = DriverManager.getConnection(JDBC,"root","10241225");
            System.out.println("connection  "+connection.isValid(5));
        } catch (SQLException e) {
            System.out.printf("NOT null open database connection +"+e.getMessage());
        }

    }
    //关闭数据库接口
    private void cloneDatabaesConnection()  {
        System.out.printf("cloneDatabaesConnection->关闭服务器同步");
        try {
            connection.close();
            System.out.printf("connection closed"+connection.isValid(5));
        } catch (SQLException e) {
            System.out.println("connection cloes error "+e.getMessage());
        }


    }

//        private int id;
//    private String uuid;
//    private String hitokoto;
//    private char type;
//    private String from;
//    private String from_who;
//    private String creator;
//    private String creator_uid;
//    private int reviewer;
//    private String commit_from;
//    private int created_at;
//    private int length;
    /**
     *
     *
     * @return
     * @author changan
     * @create  node data 需要传入一个判断字符 一个sql后半句  采用拼接组成sql查询条件
     **/
    public void insertData(String insertSql,int per, hitokotoCode data) {
        try {
            PreparedStatement  ps = connection.prepareStatement(insertSql);
            if (per == 0 ){
                ps.setInt(1,data.getId());
                ps.setString(2,data.getUuid());
                ps.setString(3,data.getHitokoto());
                ps.setByte(4,(byte)data.getType());
                ps.setString(5,data.getFrom());
                ps.setString(6,data.getFrom_who());
                ps.setString(7,data.getCreator());
//                ps.setTimestamp(8,new Timestamp(System.currentTimeMillis()));
                ps.setString(8,data.getCreator_uid());
                ps.setInt(9,data.getReviewer());
                ps.setString(10,data.getCommit_from());
                ps.setInt(11,data.getCreated_at());
                ps.setInt(12,data.getLength());
                ps.execute();
            }else if(per == 1){
                ps.setInt(1,data.getId());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}
