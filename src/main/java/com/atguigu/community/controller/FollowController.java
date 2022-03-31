package com.atguigu.community.controller;

import com.atguigu.community.entity.Page;
import com.atguigu.community.entity.User;
import com.atguigu.community.service.FollowService;
import com.atguigu.community.service.UserService;
import com.atguigu.community.util.CommunityConstant;
import com.atguigu.community.util.CommunityUtil;
import com.atguigu.community.util.HostHolder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
public class FollowController {
    @Autowired
    private FollowService followService;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private UserService userService;

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

    @RequestMapping(path = "/followees/{userId}",method = RequestMethod.GET)
    public String getFollowees(@PathVariable("userId")int userId, Model model, Page page){
        User user = userService.findUserById(userId);
        if(user == null){
            throw new RuntimeException("该用户不存在");
        }
        model.addAttribute("user",user);
        page.setLimit(5);
        page.setPath("/followees/"+userId);
        page.setRows((int)followService.findFolloweeCount(userId, CommunityConstant.ENTITY_TYPE_USER));
        //model.addAttribute("total",followService.findFolloweeCount(userId, CommunityConstant.ENTITY_TYPE_USER));
        List<Map<String,Object>> userList = followService.findFollowees(userId,page.getOffset(),page.getLimit());
        if(userList != null){
            for (Map<String,Object> map:userList){
                User u = (User) map.get("user");
                map.put("hasFollowed",hasFollowed(u.getId()));

            }
        }
        model.addAttribute("users",userList);
        return "/site/followee";
    }

    @RequestMapping(path = "/followers/{userId}",method = RequestMethod.GET)
    public String getFollowers(@PathVariable("userId")int userId, Model model,Page page){
        User user = userService.findUserById(userId);
        if(user == null){
            throw new RuntimeException("该用户不存在");
        }
        model.addAttribute("user",user);
        page.setLimit(5);
        page.setPath("/followees/"+userId);
        page.setRows((int)followService.findFolloweeCount(userId, CommunityConstant.ENTITY_TYPE_USER));
        //model.addAttribute("total",followService.findFollowerCount(CommunityConstant.ENTITY_TYPE_USER, userId));
        List<Map<String,Object>> userList = followService.findFollowers(userId,page.getOffset(),page.getLimit());
        if(userList != null){
            for (Map<String,Object> map:userList){
                User u = (User) map.get("user");
                map.put("hasFollowed",hasFollowed(u.getId()));

            }
        }
        model.addAttribute("users",userList);
        return "/site/follower";
    }
    private boolean hasFollowed(int userId){
        if(hostHolder.getUser() == null){
            return false;
        }
        return followService.hasFollowed(hostHolder.getUser().getId(),CommunityConstant.ENTITY_TYPE_USER,userId);
    }

}
