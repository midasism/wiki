package com.example.wiki.controller;

import com.example.wiki.entity.Test;
import com.example.wiki.service.TestService;
import com.example.wiki.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
public class TestController {
    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

    @Resource
    private RedisTemplate redisTemplate;

    @Value("${test.hello}")
    private String hello;

    @Resource
    private TestService testService;

    @RequestMapping("/hello")
    public String hello() {
        return "Hello World" + hello;
    }

    @PostMapping("/hello/post")
    public String helloPost(String name) {
        return "Post Test:" + name;
    }

    @RequestMapping("/test/list")
    public List<Test> list() {
        return testService.list();
    }

    @RequestMapping("/redis/set/{key}/{value}")
    public String set(@PathVariable Long key, @PathVariable String value) {
        redisTemplate.opsForValue().set(key, value);
        LOG.info("key:{}  value:{}", key, value);
        return "redis set success";
    }

    @RequestMapping("/redis/get/{key}")
    public Object get(@PathVariable Long key) {
        Object o = redisTemplate.opsForValue().get(key);
        LOG.info("key:{}  value:{}", key, o);
        return o;
    }
}

