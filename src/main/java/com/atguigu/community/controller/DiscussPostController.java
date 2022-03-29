package com.atguigu.community.controller;

import com.atguigu.community.Dao.DiscussPostMapper;
import com.atguigu.community.entity.Comment;
import com.atguigu.community.entity.DiscussPost;
import com.atguigu.community.entity.User;
import com.atguigu.community.VO.CommentVO;
import com.atguigu.community.service.CommentService;
import com.atguigu.community.service.LikeService;
import com.atguigu.community.service.UserService;
import com.atguigu.community.util.CommunityConstant;
import com.atguigu.community.util.CommunityUtil;
import com.atguigu.community.util.HostHolder;
import com.atguigu.community.util.SensitiveFilter;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/discuss")
public class DiscussPostController implements CommunityConstant {
    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private CommentService commentService;

    @Autowired
    private SensitiveFilter sensitiveFilter;
    @Autowired
    private LikeService likeService;

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    public String addDiscussPost(String title, String content) {
        User user = hostHolder.getUser();
        if (user == null) {
            return CommunityUtil.getJsonString(403, "您未登录");
        }
        DiscussPost discussPost = new DiscussPost();
        discussPost.setContent(content);
        discussPost.setTitle(title);
        discussPost.setUserId(user.getId());
        discussPost.setCreateTime(new Date());
        discussPostMapper.insertDiscussPost(discussPost);
        return CommunityUtil.getJsonString(0, "发布成功");
    }

    @RequestMapping(value = "/detail/{discussPostId}",method = RequestMethod.GET)
    public String getDiscussPost(@PathVariable("discussPostId") int id, Model model,
                                 @RequestParam(required = false,defaultValue = "1")int pageNum,
                                 @RequestParam(required = false,defaultValue = "5")int pageSize){
        //帖子
        DiscussPost discussPost = discussPostMapper.selectDiscussPostById(id);
        model.addAttribute("post",discussPost);
        //作者
        User user = userService.findUserById(discussPost.getUserId());
        //点赞数量
        long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST,id);
        model.addAttribute("likeCount",likeCount);
        //点赞状态
        int likeStatus = hostHolder.getUser() == null? 0:
        likeService.findEntityLikeStatus(hostHolder.getUser().getId(),ENTITY_TYPE_POST,id);
        model.addAttribute("likeStatus",likeStatus);
        model.addAttribute("user",user);

        //评论信息，帖子的评论
        PageInfo<Comment> pageInfo = commentService.findCommentsByEntity(ENTITY_TYPE_POST,discussPost.getId(),pageNum,pageSize);
        List<Comment> commentList = pageInfo.getList();

        List<CommentVO> commentVOList = new ArrayList<>();

        //评论：给帖子的评论
        //回复：给评论的评论
        for (Comment comment:commentList){
            //每条评论的user信息
            int userId = comment.getUserId();
            User userById = userService.findUserById(userId);
            //点赞数量
            long likeCount1 = likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT,comment.getId());
            //点赞状态
            int likeStatus1 = hostHolder.getUser() == null? 0:
                    likeService.findEntityLikeStatus(hostHolder.getUser().getId(),ENTITY_TYPE_COMMENT,comment.getId());

            //每条评论的回复信息
            PageInfo<Comment> replyInfo = commentService.findCommentsByEntity(ENTITY_TYPE_COMMENT, comment.getId(), 1, Integer.MAX_VALUE);
            List<Comment> replyList = replyInfo.getList();
            int replyCount = (int) replyInfo.getTotal();

            List<CommentVO> replyVOList = new ArrayList<>();

            if (replyVOList != null) {
                //每条回复的信息
                for(Comment reply:replyList){
                    User user1 = userService.findUserById(reply.getUserId());
                    User targetUser = userService.findUserById(reply.getTargetId());
                    //点赞数量
                    long likeCount2 = likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT,reply.getId());
                    //点赞状态
                    int likeStatus2 = hostHolder.getUser() == null? 0:
                            likeService.findEntityLikeStatus(hostHolder.getUser().getId(),ENTITY_TYPE_COMMENT,reply.getId());

                    CommentVO replyVO = new CommentVO();
                    replyVO.setTargetUser(targetUser);
                    replyVO.setUser(user1);
                    replyVO.setComment(reply);
                    replyVO.setLikeCount(likeCount2);
                    replyVO.setLikeStatus(likeStatus2);
                    replyVOList.add(replyVO);
                }
            }

            //自定义的类，返回给前端
            CommentVO commentVO = new CommentVO();
            commentVO.setUser(userById);
            commentVO.setComment(comment);
            commentVO.setReplyVOList(replyVOList);
            commentVO.setCount(replyCount);
            commentVO.setLikeCount(likeCount1);
            commentVO.setLikeStatus(likeStatus1);
            commentVOList.add(commentVO);
        }
        model.addAttribute("commentVOList",commentVOList);
        model.addAttribute("pageInfo",pageInfo);
        return"/site/discuss-detail";
    }

}
