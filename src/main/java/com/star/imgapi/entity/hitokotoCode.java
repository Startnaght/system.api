package com.star.imgapi.entity;

import lombok.Data;

//  "id": 962,
//          "uuid": "e5dfbf51-3438-4c4d-8773-d94defaee0e8",
//          "hitokoto": "我和你，可以做朋友吗？",
//          "type": "b",
//          "from": "声の形",
//          "from_who": null,
//          "creator": "树形图设计者",
//          "creator_uid": 55,
//          "reviewer": 0,
//          "commit_from": "web",
//          "created_at": "1479435961",
//          "length": 11
@Data
public class hitokotoCode {
    private int id;
    private String uuid;
    private String hitokoto;
    private char type;
    private String from;
    private String from_who;
    private String creator;
    private String creator_uid;
    private int reviewer;
    private String commit_from;
    private int created_at;
    private int length;

}
