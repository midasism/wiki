package com.example.wiki.service;

import com.example.wiki.entity.User;
import com.example.wiki.entity.UserExample;
import com.example.wiki.mapper.UserMapper;
import com.example.wiki.req.UserQueryReq;
import com.example.wiki.req.UserSaveReq;
import com.example.wiki.resp.UserQueryResp;
import com.example.wiki.resp.PageResp;
import com.example.wiki.util.CopyUtil;
import com.example.wiki.util.SnowFlake;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.List;

@Service
public class UserService {

    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

    @Resource
    private UserMapper userMapper;

    @Resource
    private SnowFlake snowFlake;

    public PageResp<UserQueryResp> list(UserQueryReq req) {
        UserExample example = new UserExample();
        //相当于where语句
        UserExample.Criteria criteria = example.createCriteria();
        //实现：根据LoginName模糊查询
        if (!ObjectUtils.isEmpty(req.getLoginName())) {
            criteria.andLoginNameLike("%" + req.getLoginName() + "%");
        }
        //PageHelper根据前端的分页参数进行分页
        PageHelper.startPage(req.getPage(), req.getSize());
        List<User> users = userMapper.selectByExample(example);
        //传入查询到的数据集合 获取分页信息
        PageInfo<User> pageInfo = new PageInfo<>(users);
        LOG.info("总行数:{}", pageInfo.getTotal());
        LOG.info("总页数:{}", pageInfo.getPages());
        List<UserQueryResp> dataList = CopyUtil.copyList(users, UserQueryResp.class);
        //分页对象 封装了分页数据和总条数 前端拿到总条数才能分页
        PageResp<UserQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(dataList);
        return pageResp;
    }

    /**
     * 新增、更新
     *
     * @param req：请求参数
     */
    public void save(UserSaveReq req) {
        User user = CopyUtil.copy(req, User.class);
        //id为空：新增
        if (ObjectUtils.isEmpty(req.getId())) {
            long id = snowFlake.nextId();
            user.setId(id);
            userMapper.insert(user);
        }
        //id不为空：更新
        else {
            userMapper.updateByPrimaryKey(user);
        }
    }

    public void delete(long id) {
        userMapper.deleteByPrimaryKey(id);
    }
}
