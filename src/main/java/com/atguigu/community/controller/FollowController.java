package com.atguigu.community.controller;

import com.atguigu.community.entity.User;
import com.atguigu.community.service.FollowService;
import com.atguigu.community.util.CommunityUtil;
import com.atguigu.community.util.HostHolder;
import org.apache.catalina.Host;
import org.apache.ibatis.scripting.xmltags.ChooseSqlNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class FollowController {
    @Autowired
    private FollowService followService;
    @Autowired
    private HostHolder hostHolder;

    @RequestMapping(path = "/follow",method = RequestMethod.POST)
    @ResponseBody
    public String follow(int entityType,int entityId){
        User user = hostHolder.getUser();
        if(user == null){
            throw new RuntimeException("当前未登录");
        }
        followService.follow(user.getId(),entityType,entityId);
        return CommunityUtil.getJsonString(0,"已关注!");
    }

    @RequestMapping(path = "/unfollow",method = RequestMethod.POST)
    @ResponseBody
    public String unfollow(int entityType,int entityId){
        User user = hostHolder.getUser();
        if(user == null){
            throw new RuntimeException("当前未登录");
        }
        followService.unfollow(user.getId(),entityType,entityId);
        return CommunityUtil.getJsonString(0,"已取消关注!");
    }
}
