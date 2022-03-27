package com.atguigu.community.service;

import com.atguigu.community.Dao.MessageMapper;
import com.atguigu.community.entity.Message;
import com.atguigu.community.util.SensitiveFilter;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class MessageService {
    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;
    public List<Message> findConversations(int userId, int pageNum, int pageSize){
        PageHelper.startPage(pageNum,pageSize);
        List<Message> messageList = messageMapper.selectConversations(userId);
        return messageList;
    }

    public int findConversationCount(int userId){
        return messageMapper.selectConversationCount(userId);
    }


    public List<Message> findLetters(String conversationId,int pageNum,int pageSize){
        PageHelper.startPage(pageNum,pageSize);
        return messageMapper.selectLetters(conversationId);
    }

    public int findLetterCount(String conversationId){
        return messageMapper.selectLetterCount(conversationId);
    }

    public int findLetterUnread(int userId,String conversationId){
        return messageMapper.selectLetterUnreadCount(userId,conversationId);
    }

    public int addMessage(Message message){
        message.setContent(HtmlUtils.htmlEscape(message.getContent()));
        message.setContent(sensitiveFilter.filter(message.getContent()));
        return messageMapper.insertMessage(message);
    }
    //改为已读，用1表示
    public int readMessage(List<Integer> ids,int status){
        return messageMapper.updateStatus(ids, 1);
    }
}
