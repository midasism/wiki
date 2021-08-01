package com.example.wiki.job;

import com.example.wiki.service.EbookSnapshotService;
import com.example.wiki.util.SnowFlake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;


@Component
public class EbookSnapshotJob {
    private static final Logger LOG = LoggerFactory.getLogger(EbookSnapshotJob.class);

    @Resource
    EbookSnapshotService ebookSnapshotService;

    @Resource
    SnowFlake snowFlake;


    /**
     * 每天0点初始化电子书快照表
     */
//    @Scheduled(cron = "0 0 0 * * ? ")
    @Scheduled(cron = "0/5 * * * * ? ")
    public void initEbookSnapShot() {
        MDC.put("LOG_ID", String.valueOf(snowFlake.nextId()));
        LOG.info("开始初始化电子书快照信息，执行sql");
        long start = System.currentTimeMillis();
        ebookSnapshotService.initEbookSnapShot();
        LOG.info("初始化电子书快照信息结束，耗时：{} 毫秒", System.currentTimeMillis() - start);
    }
}
