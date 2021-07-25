package com.example.wiki.controller;

import com.example.wiki.req.DocQueryReq;
import com.example.wiki.req.DocSaveReq;
import com.example.wiki.resp.CommonResp;
import com.example.wiki.resp.DocQueryResp;
import com.example.wiki.resp.PageResp;
import com.example.wiki.service.DocService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/doc")
public class DocController {

    @Resource
    private DocService docService;


    @RequestMapping("/list")
    public CommonResp list(@Valid DocQueryReq req) {
        CommonResp<PageResp<DocQueryResp>> resp = new CommonResp<>();
        PageResp<DocQueryResp> list = docService.list(req);
        resp.setContent(list);
        return resp;
    }

    @RequestMapping("/all/{ebookId}")
    public CommonResp all(@PathVariable Long ebookId) {
        CommonResp<List<DocQueryResp>> resp = new CommonResp<>();
        List<DocQueryResp> all = docService.all(ebookId);
        resp.setContent(all);
        return resp;
    }

    @PostMapping("/save")
    public CommonResp save(@Valid @RequestBody DocSaveReq req) {
        CommonResp resp = new CommonResp<>();
        docService.save(req);
        return resp;
    }

    @DeleteMapping("/delete/{ids}")
    public CommonResp delete(@PathVariable String ids) {
        CommonResp resp = new CommonResp<>();
        List<String> tempStr = Arrays.asList(ids.split(","));
        List<Long> idsStr = new ArrayList<>(tempStr.size());
        int a = tempStr.size();
        for (int i = 0; i < tempStr.size(); i++) {
            idsStr.add(Long.parseLong(tempStr.get(i)));
        }
        docService.delete(idsStr);
        return resp;
    }

    @GetMapping("/find-content/{id}")
    public CommonResp findContent(@PathVariable Long id) {
        CommonResp<String> resp = new CommonResp<>();
        String content = docService.findContent(id);
        resp.setContent(content);
        return resp;
    }

    @GetMapping("/queryCount/{id}")
    public CommonResp queryViewCount(@PathVariable Long id) {
        CommonResp<DocQueryResp> resp = new CommonResp<>();
        DocQueryResp docQueryResp = docService.queryCount(id);
        resp.setContent(docQueryResp);
        return resp;
    }

    @PostMapping("/vote/{id}")
    public CommonResp vote(@PathVariable Long id) {
        CommonResp<Integer> resp = new CommonResp<>();
        docService.vote(id);
        return resp;
    }
}

