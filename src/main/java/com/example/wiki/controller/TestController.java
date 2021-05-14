package com.example.wiki.controller;

import com.example.wiki.entity.Test;
import com.example.wiki.service.TestService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
public class TestController {
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
}

