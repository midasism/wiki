package com.example.wiki.controller;

import com.example.wiki.req.UserLoginReq;
import com.example.wiki.req.UserQueryReq;
import com.example.wiki.req.UserResetPasswordReq;
import com.example.wiki.req.UserSaveReq;
import com.example.wiki.resp.CommonResp;
import com.example.wiki.resp.PageResp;
import com.example.wiki.resp.UserLoginResp;
import com.example.wiki.resp.UserQueryResp;
import com.example.wiki.service.UserService;
import com.example.wiki.util.SnowFlake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/user")
public class UserController {

    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

    @Resource
    private UserService userService;

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private SnowFlake snowFlake;


    @RequestMapping("/list")
    public CommonResp list(@Valid UserQueryReq req) {
        CommonResp<PageResp<UserQueryResp>> resp = new CommonResp<>();
        PageResp<UserQueryResp> list = userService.list(req);
        resp.setContent(list);
        return resp;
    }

    @PostMapping("/save")
    public CommonResp save(@Valid @RequestBody UserSaveReq req) {
        //将密码用md5加密后再存储到数据库
        req.setPassword(DigestUtils.md5DigestAsHex(req.getPassword().getBytes()));
        CommonResp resp = new CommonResp<>();
        userService.save(req);
        return resp;
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp delete(@PathVariable long id) {
        CommonResp resp = new CommonResp<>();
        userService.delete(id);
        return resp;
    }

    @PostMapping("/reset-password")
    public CommonResp resetPassword(@Valid @RequestBody UserResetPasswordReq req) {
        //将密码用md5加密后再存储到数据库
        req.setPassword(DigestUtils.md5DigestAsHex(req.getPassword().getBytes()));
        CommonResp resp = new CommonResp<>();
        userService.resetPassword(req);
        return resp;
    }

    @PostMapping("/login")
    public CommonResp login(@Valid @RequestBody UserLoginReq req) {
        //将密码用md5加密后再存储到数据库
        req.setPassword(DigestUtils.md5DigestAsHex(req.getPassword().getBytes()));
        CommonResp resp = new CommonResp<>();
        UserLoginResp userLoginResp = userService.login(req);

        Long token = snowFlake.nextId();
        LOG.info("生成单点登录token {}，存入redis 时间设为24小时", token);
        //将一个类放入redis 需要支持序列化
        //也可以直接将对象转化为json数据 就不需要序列化了  JSONObject.toJSONString(userLoginResp)
        redisTemplate.opsForValue().set(token, userLoginResp, 3600 * 24, TimeUnit.SECONDS);
        userLoginResp.setToken(token.toString());

        resp.setContent(userLoginResp);
        return resp;
    }

    @GetMapping("/logout/{token}")
    public CommonResp login(@PathVariable String token) {
        CommonResp resp = new CommonResp<>();
        redisTemplate.delete(token);
        LOG.info("从redis中删除token：{}",token);
        return resp;
    }
}

