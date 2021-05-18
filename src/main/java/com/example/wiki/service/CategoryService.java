package com.example.wiki.service;

import com.example.wiki.entity.Category;
import com.example.wiki.entity.CategoryExample;
import com.example.wiki.mapper.CategoryMapper;
import com.example.wiki.req.CategoryQueryReq;
import com.example.wiki.req.CategorySaveReq;
import com.example.wiki.resp.CategoryQueryResp;
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
public class CategoryService {

    private static final Logger LOG = LoggerFactory.getLogger(CategoryService.class);

    @Resource
    private CategoryMapper categoryMapper;

    @Resource
    private SnowFlake snowFlake;

    /**
     * 分页获取数据
     * @param req
     * @return
     */
    public PageResp<CategoryQueryResp> list(CategoryQueryReq req) {
        CategoryExample example = new CategoryExample();
        example.setOrderByClause("sort asc");
//        //相当于where语句
        CategoryExample.Criteria criteria = example.createCriteria();
        //实现：根据name模糊查询
        if (!ObjectUtils.isEmpty(req.getName())) {
            criteria.andNameLike("%" + req.getName() + "%");
        }
        //PageHelper根据前端的分页参数进行分页
        PageHelper.startPage(req.getPage(), req.getSize());
        List<Category> categorys = categoryMapper.selectByExample(example);
        //传入查询到的数据集合 获取分页信息
        PageInfo<Category> pageInfo = new PageInfo<>(categorys);
        LOG.info("总行数:{}", pageInfo.getTotal());
        LOG.info("总页数:{}", pageInfo.getPages());

        List<CategoryQueryResp> dataList = CopyUtil.copyList(categorys, CategoryQueryResp.class);
        //分页对象 封装了分页数据和总条数 前端拿到总条数才能分页
        PageResp<CategoryQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(dataList);
        return pageResp;
    }

    /**
     * 获取全表数据
     * @return
     */
    public List<CategoryQueryResp> all() {
        CategoryExample example = new CategoryExample();
        example.setOrderByClause("sort asc");
        List<Category> categorys = categoryMapper.selectByExample(example);
        List<CategoryQueryResp> dataList = CopyUtil.copyList(categorys, CategoryQueryResp.class);
        return dataList;
    }

    /**
     * 新增、更新
     *
     * @param req：请求参数
     */
    public void save(CategorySaveReq req) {
        Category category = CopyUtil.copy(req, Category.class);
        //id为空：新增
        if (ObjectUtils.isEmpty(req.getId())) {
            long id = snowFlake.nextId();
            category.setId(id);
            categoryMapper.insert(category);
        }
        //id不为空：更新
        else {
            categoryMapper.updateByPrimaryKey(category);
        }
    }

    public void delete(long id) {
        categoryMapper.deleteByPrimaryKey(id);
    }
}
