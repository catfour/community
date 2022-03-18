package com.atguigu.community.service;

import com.atguigu.community.Dao.LoginTicketMapper;
import com.atguigu.community.Dao.UserMapper;
import com.atguigu.community.entity.LoginTicket;
import com.atguigu.community.entity.User;
import com.atguigu.community.util.CommunityConstant;
import com.atguigu.community.util.CommunityUtil;
import com.atguigu.community.util.MailClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class UserService implements CommunityConstant {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private TemplateEngine templateEngine;

    @Value(value = "${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private MailClient mailClient;

    @Autowired
    private LoginTicketMapper loginTicketMapper;

    public User findUserById(int id) {
        return userMapper.selectById(id);
    }

    /**
     * 用户注册
     * @param user
     * @return
     */
    public Map<String,Object> register(User user){
        Map<String,Object> map = new HashMap<>();
        if(user == null){
            throw new IllegalArgumentException("参数不能为空！！");
        }
        if(StringUtils.isBlank(user.getUsername())){
            map.put("usernameMsg","账号不能为空!");
            return map;
        }
        if(StringUtils.isBlank(user.getPassword())){
            map.put("passwordMsg","密码不能为空!");
            return map;
        }
        if(StringUtils.isBlank(user.getEmail())){
            map.put("emailMsg","邮箱不能为空!");
            return map;
        }
        //验证账号
        User user1 = userMapper.selectByName(user.getUsername());
        if(user1 != null){
            map.put("usernameMsg","账号已存在!!");
            return map;
        }
        user1 = userMapper.selectByEmail(user.getEmail());
        if (user1 != null){
            map.put("emailMsg","该邮箱已被注册！");
            return map;
        }

        //注册账号
        //生成随机字符串
        user.setSalt(CommunityUtil.generateUUID().substring(0,10));
        //设置密码
        user.setPassword(CommunityUtil.md5(user.getPassword() + user.getSalt()));
        //设置激活状态
        user.setStatus(0);
        //设置用户类型
        user.setType(0);
        //设置用户头像
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png",new Random().nextInt(1000)));
        //生成激活码
        user.setActivationCode(CommunityUtil.generateUUID());
        //设置创建时间
        user.setCreateTime(new Date());

        userMapper.insertUser(user);

        //发送激活链接
        Context context = new Context();
        context.setVariable("username",user.getUsername());
        //需要拼接http://www.nowcoder.com/activation/abcdefg123456.html
        String url = domain + contextPath +"/activation/" + user.getId() +"/"+user.getActivationCode();

        context.setVariable("activationUrl",url);
        //模板引擎处理
        String content = templateEngine.process("/mail/activation",context);
        mailClient.sendMail(user.getEmail(),"激活账号",content);

        return map;
    }


    public int activation(int userId,String code){
        User user = userMapper.selectById(userId);
        System.out.println(user);
        if(user.getStatus() ==  1){
            return ACTIVATION_REPEAT;
        }else if(user.getActivationCode().equals(code)){
            userMapper.updateStatus(1,userId);
            return ACTIVATION_SUCCESS;
        }else{
            return ACTIVATION_FAILURE;
        }
    }

    public Map<String,Object> login(String username,String password, int expireSeconds){
        Map<String,Object> map = new HashMap<>();
        if(StringUtils.isBlank(username)){
            map.put("usernameMsg","用户名不能为空！");
            return map;
        }
        if(StringUtils.isBlank(password)){
            map.put("usernameMsg","用户名不能为空！");
            return map;
        }
        //验证账号
        User user = userMapper.selectByName(username);
        if(user == null){
            map.put("usernameMsg","该账户不存在！");
            return map;
        }
        //验证状态
        if(user.getStatus() == 0){
            map.put("usernameMsg","该账户未激活！");
            return map;
        }
        //验证密码
        if(!user.getPassword().equals(CommunityUtil.md5(password+user.getSalt()))){
            map.put("passwordMsg","密码不正确！");
            return map;
        }
        //生成登录凭证
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setStatus(0);
        loginTicket.setTicket(CommunityUtil.generateUUID());
        loginTicket.setUserId(user.getId());
        loginTicket.setExpired(new Date(System.currentTimeMillis() + (long)expireSeconds *1000));
        loginTicketMapper.insert(loginTicket);
        map.put("ticket",loginTicket.getTicket());

        return map;
    }
    //用户退出
    public void logout(String ticket) {
        //0代表正常，-1代表失效
        loginTicketMapper.update(ticket,-1);
    }

    public LoginTicket findLoginTicket(String ticket) {
        return loginTicketMapper.select(ticket);
    }
}
