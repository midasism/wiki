package com.example.wiki.controller;

import com.example.wiki.entity.Ebook;
import com.example.wiki.resp.CommonResp;
import com.example.wiki.service.EbookService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/ebook")
public class EbookController {

     @Resource
     private EbookService ebookService;


    @RequestMapping("/list")
    public CommonResp list() {
        CommonResp<List<Ebook>> resp=new CommonResp<>();
        List<Ebook> list = ebookService.list();
        resp.setContent(list);
        return resp;
    }
}

