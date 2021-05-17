package com.example.wiki.service;

import com.example.wiki.entity.Ebook;
import com.example.wiki.entity.EbookExample;
import com.example.wiki.mapper.EbookMapper;
import com.example.wiki.req.EbookReq;
import com.example.wiki.resp.EbookResp;
import com.example.wiki.resp.PageResp;
import com.example.wiki.util.CopyUtil;
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

    public PageResp<EbookResp> list(EbookReq req) {
        EbookExample example = new EbookExample();
        //相当于where语句
        EbookExample.Criteria criteria = example.createCriteria();
        if (!ObjectUtils.isEmpty(req.getName())) {
            criteria.andNameLike("%" + req.getName() + "%");
        }
        PageHelper.startPage(req.getPage(), req.getSize());
        List<Ebook> ebooks = ebookMapper.selectByExample(example);
        //传入查询到的数据集合 获取分页信息
        PageInfo<Ebook> pageInfo = new PageInfo<>(ebooks);
        LOG.info("总行数:{}" , pageInfo.getTotal());
        LOG.info("总页数:{}" , pageInfo.getPages());
//        for (Ebook ebook : ebooks) {
        //Spring的工具类 copyProperties复制对象
//            BeanUtils.copyProperties(ebook, ebookResp);
//            ret.add(ebookResp);

//            EbookResp copy = CopyUtil.copy(ebook, EbookResp.class);
//            ret.add(copy);
//        }
        List<EbookResp> dataList = CopyUtil.copyList(ebooks, EbookResp.class);
        //分页对象 封装了分页数据和总条数 前端拿到总条数才能分页
        PageResp<EbookResp> pageResp=new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(dataList);
        return pageResp;
    }
}
