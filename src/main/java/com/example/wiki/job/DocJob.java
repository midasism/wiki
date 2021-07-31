package com.example.wiki.job;

import com.example.wiki.service.DocService;
import com.example.wiki.util.SnowFlake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;


@Component
public class DocJob {
    private static final Logger LOG = LoggerFactory.getLogger(DocJob.class);

    @Resource
    DocService docService;

    @Resource
    SnowFlake snowFlake;

    /**
     * 每30秒更新电子书信息
     */
    @Scheduled(cron = "0 0/1 * * * ? ")
    public void cron()  {
        MDC.put("LOG_ID", String.valueOf(snowFlake.nextId()));
        LOG.info("开始更新电子书文档信息，执行sql");
        long start = System.currentTimeMillis();
        docService.updateEbookInfo();
        LOG.info("更新电子书文档信息结束，耗时：{} 毫秒", System.currentTimeMillis() - start);
    }
}
