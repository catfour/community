package com.atguigu.community.controller;

import com.atguigu.community.Dao.UserMapper;
import com.atguigu.community.annotation.LoginRequired;
import com.atguigu.community.entity.User;
import com.atguigu.community.service.LikeService;
import com.atguigu.community.service.UserService;
import com.atguigu.community.util.CommunityUtil;
import com.atguigu.community.util.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.model.IModel;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.crypto.dsig.spec.XSLTTransformParameterSpec;
import java.io.*;

@Controller
@RequestMapping("/user")
public class UserController {
    private Logger logger = LoggerFactory.getLogger(UserController.class);
    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private LikeService likeService;

    @Value("${community.path.upload}")
    private String uploadPath;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @LoginRequired
    @RequestMapping(value = "/setting",method = RequestMethod.GET)
    public String getSettingPage(){
        return "/site/setting";
    }

    @LoginRequired
    @RequestMapping(value = "/upload",method = RequestMethod.POST)
    public String uploadHeader(MultipartFile headerImage, Model model){
        if(headerImage == null){
            model.addAttribute("error","您还没有选择图片！");
            return "/site/setting";
        }
        String fileName = headerImage.getOriginalFilename();
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        if(StringUtils.isBlank(suffix)){
            model.addAttribute("error","文件格式不正确");
            return "/site/setting";
        }
        //生成随机文件名
        fileName = CommunityUtil.generateUUID() + suffix;
        //确定文件有效的路径
        File dest = new File(uploadPath + "/" + fileName);
        //存储文件
        try {
            headerImage.transferTo(dest);
        } catch (IOException e) {
            logger.error("上传文件失败，服务期发生异常" + e );
        }
        //更新当前用户的头像的路径（web访问路径）
        // http://localhost:8080/community/user/header/xxx.png
        User user = hostHolder.getUser();
        String headerUrl = domain + contextPath + "/user/header/" + fileName;
        userMapper.updateHeader(headerUrl,user.getId());

        return "redirect:/index";

    }
    @RequestMapping(value = "/header/{fileName}",method = RequestMethod.GET)
    public void getHeader(@PathVariable("fileName") String fileName, HttpServletResponse response){
        //服务器存放路径
        fileName = uploadPath + "/" + fileName;
        //文件后缀
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        //响应图片
        response.setContentType("image/" + suffix);
        try(
                FileInputStream fis = new FileInputStream(fileName);
                OutputStream os = response.getOutputStream();
                ) {
            byte [] buffer = new byte[1024];
            int b = 0;
            while (( b = fis.read(buffer)) != -1){
                os.write(buffer,0, b);
            }

        } catch (IOException e) {
            logger.error("读取头像失败：" + e.getMessage());
        }
    }
    //修改密码
    @RequestMapping(path = "/{id}", method = RequestMethod.PUT)
    public String updatePassword(@PathVariable int id, String oldPassword, String newPassword,
                                 Model model, HttpServletRequest request ){
        if(StringUtils.isBlank(oldPassword)){
            model.addAttribute("passwordMsg","原密码不能为空！！");
            return "/site/setting";
        }
        if(StringUtils.isBlank(newPassword)){
            model.addAttribute("passwordMsg","新密码不能为空！！");
            return "/site/setting";
        }

        User user = userMapper.selectById(id);
        String old = CommunityUtil.md5(oldPassword + user.getSalt());
        if(!user.getPassword().equals(old)){
            model.addAttribute("passwordMsg","原密码不正确！！");
            return "/site/setting";
        }
        //更改密码
        int res = userService.update(id,newPassword);
        if(res != 1){
            model.addAttribute("passwordMsg","密码更新失败，请重新设置！！");
            return "/site/setting";
        }
        //更新成功后
        //用户需要退出登录
        Cookie[] cookies = request.getCookies();
        String ticket = null;
        if(cookies != null){
            for (Cookie cookie:cookies) {
                if(cookie.getName().equals("ticket")){
                    ticket = cookie.getValue();
                    break;
                }
            }
        }
        if(ticket!=null){
            userService.logout(ticket);
        }
        //重定向默认是get请求
        return "redirect:/login";
    }


    //个人主页
    @RequestMapping(path = "/profile/{userId}",method = RequestMethod.GET)
    public String getProfilePage(@PathVariable("userId") int userId, Model model){
        User user = userService.findUserById(userId);
        if(user == null){
            throw new RuntimeException("该用户不存在");
        }
        //用户
        model.addAttribute("user",user);
        //点赞数量
        int likeCount = likeService.findUserLikeCount(userId);
        model.addAttribute("likeCount",likeCount);
        return "/site/profile" ;
    }
}
