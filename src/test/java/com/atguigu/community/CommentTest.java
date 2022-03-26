package com.atguigu.community;

import com.atguigu.community.entity.Comment;
import com.atguigu.community.service.CommentService;
import com.github.pagehelper.PageInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class CommentTest {
    @Autowired
    private CommentService commentService;
    @Test
    public void testSelect(){
        PageInfo<Comment> page = commentService.findCommentsByEntity(1, 274, 0, 5);
        System.out.println(page.getTotal());        //总记录条数
        System.out.println(page.getSize());         //当前页有几条纪录
        System.out.println(page.getPageNum());      //当前是第几页
        System.out.println(page.getPages());        //总共有几页
        System.out.println(page.getPageSize());     //每页的大小
        List<Comment> list = page.getList();
        for (Comment comment:list){
            System.out.println(comment);
        }

    }
}
