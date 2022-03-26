package com.atguigu.community.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Comment {
    private int id;
    private int userId;    //发出该评论的用户
    private int entityType;//实体类型，例如针对帖子发评论，针对课程，针对题目发评论等等
    private int entityId;  //代表上面类型的某一个帖子，是1，2等等
    private int targetId;  //针对哪个用户的评论，这个id是目标用户id
    private String content;
    private int status;
    private Date createTime;

}
