package com.atguigu.community;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootTest
public class RedisTest {
    @Autowired
    private RedisTemplate redisTemplate;
    @Test
    public void test01(){
        redisTemplate.opsForSet().add("user:name","张三","李四","王五");
        System.out.println(redisTemplate.opsForSet().size("user:name"));
        System.out.println(redisTemplate.opsForSet().isMember("user:name","李四"));
    }
}
