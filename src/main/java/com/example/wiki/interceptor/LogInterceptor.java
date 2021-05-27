package com.example.wiki.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 拦截器：Spring框架特有的，常用于登录校验，权限校验，请求日志打印 /login
 */
@Component
public class LogInterceptor implements HandlerInterceptor {

    @Resource
    private RedisTemplate redisTemplate;

    private static final Logger LOG = LoggerFactory.getLogger(LogInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 打印请求信息
        LOG.info("------------- LogInterceptor 开始 -------------");
        LOG.info("请求地址: {} {}", request.getRequestURL().toString(), request.getMethod());
        LOG.info("远程地址: {}", request.getRemoteAddr());
        long startTime = System.currentTimeMillis();
        request.setAttribute("requestStartTime", startTime);

        //不拦截OPTIONS请求
        //前后端分离的架构，前端会发一个OPTIONS请求做预检
        if (request.getMethod().toUpperCase().equals("OPTIONS")) {
            return true;
        }

        String path = request.getRequestURL().toString();
        LOG.info("接口登录拦截：path {}", path);

        //获取header中的token信息
        String token = request.getHeader("token");
        LOG.info("登录校验开始：token {}", token);
        if (token == null || token.isEmpty()) {
            LOG.info("token为空，请求被拦截");
            //UNAUTHORIZED对应状态码401
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return false;
        }
        Object o = redisTemplate.opsForValue().get(token);
        //没找到token
        if (ObjectUtils.isEmpty(o)) {
            LOG.info("token无效，请求被拦截");
        }else{
            LOG.info("已登录");
            return true;
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        long startTime = (Long) request.getAttribute("requestStartTime");
        LOG.info("------------- LogInterceptor 结束 耗时：{} ms -------------", System.currentTimeMillis() - startTime);
    }
}
