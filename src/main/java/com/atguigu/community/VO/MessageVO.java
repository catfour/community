package com.atguigu.community.VO;

import com.atguigu.community.entity.Message;
import com.atguigu.community.entity.User;
import lombok.Data;

@Data
public class MessageVO {
    //私信消息数量
    private int letterCount;
    //私信未读消息
    private int letterUnreadCount;
    //私信对应的用户
    private User targetUser;
    //会话中最新的消息
    private Message conversation;
}
