package com.atguigu.community;

import com.atguigu.community.Dao.LoginTicketMapper;
import com.atguigu.community.entity.LoginTicket;
import com.atguigu.community.util.CommunityUtil;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.AnnotationUtils;
import org.junit.platform.commons.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.Random;

@SpringBootTest
public class LoginTest {
    @Autowired
    private LoginTicketMapper loginTicketMapper;

    @Test
    public void testLogin(){

    }
    @Test
    public void testTicket(){
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setStatus(0);
        loginTicket.setTicket(CommunityUtil.generateUUID());
        loginTicket.setUserId(100);
        long currentTIme = System.currentTimeMillis();
        System.out.println(currentTIme);
        Date nowTime = new Date(System.currentTimeMillis());
        System.out.println(nowTime.toString());
        Date pp = new Date(System.currentTimeMillis() + (long)8640000 *1000);
        System.out.println(pp.toString());
        loginTicket.setExpired(new Date(System.currentTimeMillis() + 8640000 *1000));

       // loginTicketMapper.insert(loginTicket);
    }

}
