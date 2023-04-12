package com.zjj.reggie;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootTest
class ReggieApplicationTests {

    @Autowired
    RedisTemplate redisTemplate;

    @Test
    void contextLoads() {

        redisTemplate.opsForValue().set("code","12mnaksdgf");
        System.out.println(redisTemplate.opsForValue().get("code"));
    }

}
