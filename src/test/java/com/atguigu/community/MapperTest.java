package com.atguigu.community;


import com.atguigu.community.Dao.DiscussPostMapper;

import com.atguigu.community.Dao.UserMapper;
import com.atguigu.community.entity.DiscussPost;
import com.atguigu.community.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;


@SpringBootTest
public class MapperTest {

    @Autowired
    private DiscussPostMapper discussPostMapper;
    @Autowired
    private UserMapper userMapper;
    @Test
    public void testSelectPost(){
        List<DiscussPost> list = discussPostMapper.selectDiscussPosts(149,0,10);
        for (DiscussPost post:list) {
            System.out.println(post);
        }
        int row = discussPostMapper.selectDiscussPostsRows(149);

        System.out.println(row);
        User user = userMapper.selectById(11);
        System.out.println(user);
    }
    @Test
    public void testUser(){
        User user = new User();
        user.setUsername("wen");
        user.setEmail("123456789@qq.com");
        userMapper.insertUser(user);
        //User u = userMapper.selectByName("wen");
        int id = user.getId();
        System.out.println(id);

        User user1 = userMapper.selectById(158);
        System.out.println(user1);
    }
}
