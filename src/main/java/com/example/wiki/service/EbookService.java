package com.example.wiki.service;

import com.example.wiki.entity.Ebook;
import com.example.wiki.entity.EbookExample;
import com.example.wiki.mapper.EbookMapper;
import com.example.wiki.req.EbookReq;
import com.example.wiki.resp.EbookResp;
import com.example.wiki.util.CopyUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class EbookService {

    @Resource
    private EbookMapper ebookMapper;

    public List<EbookResp> list(EbookReq req) {
        EbookExample example = new EbookExample();
        //相当于where语句
        EbookExample.Criteria criteria = example.createCriteria();
        criteria.andNameLike("%" + req.getName() + "%");
        List<Ebook> ebooks = ebookMapper.selectByExample(example);
//        for (Ebook ebook : ebooks) {
            //Spring的工具类 copyProperties复制对象
//            BeanUtils.copyProperties(ebook, ebookResp);
//            ret.add(ebookResp);

//            EbookResp copy = CopyUtil.copy(ebook, EbookResp.class);
//            ret.add(copy);
//        }
        List<EbookResp> ret = CopyUtil.copyList(ebooks, EbookResp.class);
        return ret;
    }
}
