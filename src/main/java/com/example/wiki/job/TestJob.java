//package com.example.wiki.job;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//import java.text.SimpleDateFormat;
//import java.util.Date;
//
//
//@Component
//public class TestJob {
//
//    private static final Logger LOG = LoggerFactory.getLogger(TestJob.class);
//
//    /**
//     * 固定时间间隔 fixedRate 单位毫秒
//     * @throws InterruptedException
//     */
//    @Scheduled(fixedRate = 5000)
//    public void simple() throws InterruptedException {
//        SimpleDateFormat formatter = new SimpleDateFormat("mm:ss");
//        String dataString = formatter.format(new Date());
////        Thread.sleep(2000);
//        LOG.info("每隔5秒执行一次：{}", dataString);
//    }
//
//    /**
//     * 自定义cron表达式
//     * 单线程：只有等上一个任务执行完毕，下一次才会在下一个时间点执行，错过就错过了
//     * @throws InterruptedException
//     */
//    @Scheduled(cron = "*/2 * * * * ?")
//    public void cron() throws InterruptedException {
//        SimpleDateFormat formatter = new SimpleDateFormat("mm:ss");
//        String dataString = formatter.format(new Date());
////        Thread.sleep(1500);
//        LOG.info("每隔2秒执行一次：{}", dataString);
//    }
//}
