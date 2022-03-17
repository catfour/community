package com.atguigu.community;

import com.atguigu.community.util.MailClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;


@SpringBootTest
public class MailTest {
    @Autowired
    private MailClient mailClient;
    @Autowired
    private TemplateEngine templateEngine;
    @Test
    public void testTextMail(){
        mailClient.sendMail("1643465966@qq.com","Text","大号发给小号");
    }

    @Test
    public void testHtml(){
        Context context = new Context();
        context.setVariable("username","Yang Liu");
        String content = templateEngine.process("/mail/demo",context);
        System.out.println(content);
        mailClient.sendMail("1643465966@qq.com","快刷题",content);
    }
}
