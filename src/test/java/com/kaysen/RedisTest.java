package com.kaysen;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootTest
public class RedisTest {
    @Autowired
    private RedisTemplate<String,String> redisTemplate;

//    @Test
    public void test1(){
        redisTemplate.opsForValue().set("name", "yks");
        System.out.println(redisTemplate.opsForValue().get("mykey"));
    }
}
