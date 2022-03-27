package com.atguigu.community.controller;

import com.alibaba.druid.sql.PagerUtils;
import com.atguigu.community.VO.MessageVO;
import com.atguigu.community.entity.Message;
import com.atguigu.community.entity.Page;
import com.atguigu.community.entity.User;
import com.atguigu.community.service.MessageService;
import com.atguigu.community.service.UserService;
import com.atguigu.community.util.CommunityUtil;
import com.atguigu.community.util.HostHolder;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
public class MessageController {

    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private MessageService messageService;
    @Autowired
    private UserService userService;
    //私信列表
    @RequestMapping(path = "/letter/list",method = RequestMethod.GET)
    public String getLetterList(Model model,
                                @RequestParam(required = false,defaultValue = "1")int pageNum,
                                @RequestParam(required = false,defaultValue = "5")int pageSize){
        User user = hostHolder.getUser();
        int count = messageService.findConversationCount(user.getId());
        //会话列表，每个会话显示最新的一条消息，同时显示该回话消息数量和未读消息数量
        List<Message> conversations = messageService.findConversations(user.getId(), pageNum, pageSize);
        PageInfo<Message> pageInfo = new PageInfo<>(conversations);
        List<MessageVO> conversationList = new ArrayList<>();
        for(Message conversation:conversations){
            MessageVO messageVO = new MessageVO();
            int letterCount = messageService.findLetterCount(conversation.getConversationId());
            int letterUnreadCount = messageService.findLetterUnread(user.getId(),conversation.getConversationId());
            int targetId = user.getId() == conversation.getFromId()?conversation.getToId():conversation.getFromId();
            User targetUser = userService.findUserById(targetId);

            messageVO.setConversation(conversation);
            messageVO.setLetterCount(letterCount);
            messageVO.setLetterUnreadCount(letterUnreadCount);
            messageVO.setTargetUser(targetUser);
            conversationList.add(messageVO);
        }
        //总未读消息数，不是对应某一个会话。
        int letterUnreadCount = messageService.findLetterUnread(user.getId(),null);
        model.addAttribute("pageInfo",pageInfo);
        model.addAttribute("conversationList",conversationList);
        model.addAttribute("letterUnreadCount",letterUnreadCount);
        return "/site/letter";
    }

    @RequestMapping(path = "/letter/detail/{conversationId}",method = RequestMethod.GET)
    public String getLetterDetail(@PathVariable("conversationId") String conversationId,
                                  @RequestParam(required = false,defaultValue = "1") int pageNum,
                                  @RequestParam(required = false,defaultValue = "5") int pageSize,
                                  Model model){
        if(pageSize > 10){
            pageSize = 10;
        }

        int letterCount = messageService.findLetterCount(conversationId);
        //私信中消息记录
        List<Message> letters = messageService.findLetters(conversationId, pageNum, pageSize);
        PageInfo<Message> pageInfo = new PageInfo<>(letters);
        List<Map<String,Object> > letterList = new ArrayList<>();
        if(letters !=null){
            for(Message letter:letters){
                Map<String,Object> map = new HashMap<>();
                map.put("letter",letter);
                map.put("fromUser",userService.findUserById(letter.getFromId()));
                letterList.add(map);
            }
        }
        model.addAttribute("letterList",letterList);
        model.addAttribute("pageInfo",pageInfo);
        //查询私信目标
        model.addAttribute("targetUser",getLetterTarget(conversationId));
        //conversationId
        model.addAttribute("conversationId",conversationId);
        //设置已读
        List<Integer> ids = getLetterIds(letters);
        if(!ids.isEmpty()){
            messageService.readMessage(ids,1);

        }
        return "/site/letter-detail";
    }

    private User getLetterTarget(String conversationId){
        String [] ids = conversationId.split("_");
        int id0 = Integer.parseInt(ids[0]);
        int id1 = Integer.parseInt(ids[1]);
        if(hostHolder.getUser().getId() == id0){
            return userService.findUserById(id1);
        }else {
            return userService.findUserById(id0);
        }

    }

    private List<Integer> getLetterIds(List<Message> letterList){
        List<Integer> ids = new ArrayList<>();
        if(letterList!=null){
            for(Message message:letterList){
                if(hostHolder.getUser().getId() == message.getToId() && message.getStatus()==0){
                    ids.add(message.getId());
                }
            }
        }
        return ids;
    }
    @RequestMapping(path = "/letter/send",method = RequestMethod.POST)
    @ResponseBody
    public String sendLetter(String toName,String content){
        User target = userService.findByName(toName);
        if(target==null){
            return CommunityUtil.getJsonString(1,"目标用户不存在");
        }
        Message message = new Message();
        message.setFromId(hostHolder.getUser().getId());
        message.setToId(target.getId());
        if(message.getFromId() < message.getToId()){
            message.setConversationId(message.getFromId()+"_"+message.getToId());
        }else{
            message.setConversationId(message.getToId()+"_"+message.getFromId());
        }
        message.setContent(content);
        message.setCreateTime(new Date());
        //0表示未读
        message.setStatus(0);
        messageService.addMessage(message);
        return CommunityUtil.getJsonString(0);
    }

}

