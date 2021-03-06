package com.example.wiki.service;

import com.example.wiki.entity.Ebook;
import com.example.wiki.entity.EbookExample;
import com.example.wiki.mapper.EbookMapper;
import com.example.wiki.req.EbookQueryReq;
import com.example.wiki.req.EbookSaveReq;
import com.example.wiki.resp.EbookQueryResp;
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
public class EbookService {

    private static final Logger LOG = LoggerFactory.getLogger(EbookService.class);

    @Resource
    private EbookMapper ebookMapper;

    @Resource
    private SnowFlake snowFlake;

    public PageResp<EbookQueryResp> list(EbookQueryReq req) {
        EbookExample example = new EbookExample();
        //相当于where语句
        EbookExample.Criteria criteria = example.createCriteria();
        //实现：根据name模糊查询
        if (!ObjectUtils.isEmpty(req.getName())) {
            criteria.andNameLike("%" + req.getName() + "%");
        }
        //实现：根据分类二查询电子书
        if (!ObjectUtils.isEmpty(req.getCategory2Id())) {
            criteria.andCategory2IdEqualTo(req.getCategory2Id());
        }
        //PageHelper根据前端的分页参数进行分页
        PageHelper.startPage(req.getPage(), req.getSize());
        List<Ebook> ebooks = ebookMapper.selectByExample(example);
        //传入查询到的数据集合 获取分页信息
        PageInfo<Ebook> pageInfo = new PageInfo<>(ebooks);
        LOG.info("总行数:{}", pageInfo.getTotal());
        LOG.info("总页数:{}", pageInfo.getPages());
//        for (Ebook ebook : ebooks) {
        //Spring的工具类 copyProperties复制对象
//            BeanUtils.copyProperties(ebook, ebookResp);
//            ret.add(ebookResp);

//            EbookResp copy = CopyUtil.copy(ebook, EbookResp.class);
//            ret.add(copy);
//        }
        List<EbookQueryResp> dataList = CopyUtil.copyList(ebooks, EbookQueryResp.class);
        //分页对象 封装了分页数据和总条数 前端拿到总条数才能分页
        PageResp<EbookQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(dataList);
        return pageResp;
    }

    /**
     * 新增、更新
     *
     * @param req：请求参数
     */
    public void save(EbookSaveReq req) {
        Ebook ebook = CopyUtil.copy(req, Ebook.class);
        //id为空：新增
        if (ObjectUtils.isEmpty(req.getId())) {
            long id = snowFlake.nextId();
            ebook.setId(id);
            ebook.setDocCount(0);
            ebook.setViewCount(0);
            ebook.setVoteCount(0);
            ebookMapper.insert(ebook);
        }
        //id不为空：更新
        else {
            ebookMapper.updateByPrimaryKey(ebook);
        }
    }

    public void delete(long id) {
        ebookMapper.deleteByPrimaryKey(id);
    }
}
