package com.atguigu.community.VO;

import com.atguigu.community.entity.Comment;
import com.atguigu.community.entity.User;
import lombok.Data;

import java.util.List;

@Data
public class CommentVO {
    //用户信息
    private User user;

    //每条评论相关的信息
    private Comment comment;

    //目标用户，
    private User targetUser;

    //记录条数
    private int count;

    //每条评论对应的回复列表,每条回复
    private List<CommentVO> replyVOList;

}
