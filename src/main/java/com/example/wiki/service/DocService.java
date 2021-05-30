package com.example.wiki.service;

import com.example.wiki.entity.Content;
import com.example.wiki.entity.Doc;
import com.example.wiki.entity.DocExample;
import com.example.wiki.mapper.ContentMapper;
import com.example.wiki.mapper.DocMapper;
import com.example.wiki.mapper.OtherDocMapper;
import com.example.wiki.req.DocQueryReq;
import com.example.wiki.req.DocSaveReq;
import com.example.wiki.resp.DocQueryResp;
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
public class DocService {

    private static final Logger LOG = LoggerFactory.getLogger(DocService.class);

    @Resource
    private DocMapper docMapper;

    @Resource
    private ContentMapper contentMapper;

    @Resource
    private OtherDocMapper otherDocMapper;

    @Resource
    private SnowFlake snowFlake;

    /**
     * 分页获取数据
     *
     * @param req
     * @return
     */
    public PageResp<DocQueryResp> list(DocQueryReq req) {
        DocExample example = new DocExample();
        example.setOrderByClause("sort asc");
//        //相当于where语句
        DocExample.Criteria criteria = example.createCriteria();
        //实现：根据name模糊查询
        if (!ObjectUtils.isEmpty(req.getName())) {
            criteria.andNameLike("%" + req.getName() + "%");
        }
        //PageHelper根据前端的分页参数进行分页
        PageHelper.startPage(req.getPage(), req.getSize());
        List<Doc> docs = docMapper.selectByExample(example);
        //传入查询到的数据集合 获取分页信息
        PageInfo<Doc> pageInfo = new PageInfo<>(docs);
        LOG.info("总行数:{}", pageInfo.getTotal());
        LOG.info("总页数:{}", pageInfo.getPages());

        List<DocQueryResp> dataList = CopyUtil.copyList(docs, DocQueryResp.class);
        //分页对象 封装了分页数据和总条数 前端拿到总条数才能分页
        PageResp<DocQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(dataList);
        return pageResp;
    }

    /**
     * 获取全表数据
     *
     * @return
     */
    public List<DocQueryResp> all(Long id) {
        DocExample example = new DocExample();
        example.setOrderByClause("sort asc");
        //        //相当于where语句
        DocExample.Criteria criteria = example.createCriteria();
        //实现：根据id查询
        criteria.andEbookIdEqualTo(id);
        List<Doc> docs = docMapper.selectByExample(example);
        List<DocQueryResp> dataList = CopyUtil.copyList(docs, DocQueryResp.class);
        return dataList;
    }

    /**
     * 新增、更新
     *
     * @param req：请求参数
     */
    public void save(DocSaveReq req) {
        Doc doc = CopyUtil.copy(req, Doc.class);
        Content content = CopyUtil.copy(req, Content.class);
        //id为空：新增
        if (ObjectUtils.isEmpty(req.getId())) {
            long id = snowFlake.nextId();
            doc.setId(id);
            //将阅读数和点赞数初始化为0
            doc.setViewCount(0);
            doc.setVoteCount(0);
            docMapper.insert(doc);
            content.setId(id);
            contentMapper.insert(content);
        }
        //id不为空：更新
        else {
            docMapper.updateByPrimaryKey(doc);
            //富文本更新 大字段
            Content content1 = contentMapper.selectByPrimaryKey(doc.getId());
            if (content1 == null) {
                content.setId(doc.getId());
                contentMapper.insert(content);
            } else {
                contentMapper.updateByPrimaryKeyWithBLOBs(content);
            }
        }
    }

    public void delete(long id) {
        docMapper.deleteByPrimaryKey(id);
    }

    public void delete(List<Long> ids) {
        DocExample example = new DocExample();
        DocExample.Criteria criteria = example.createCriteria();
        criteria.andIdIn(ids);
        docMapper.deleteByExample(example);
    }

    public String findContent(long id) {
        //每访问文档一次 阅读数就+1
        otherDocMapper.incrementViewCount(id);
        Content content = contentMapper.selectByPrimaryKey(id);
        if (ObjectUtils.isEmpty(content)) {
            return "";
        } else {
            return content.getContent();
        }
    }
}
