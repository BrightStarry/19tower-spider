package com.liantong.spider.task;

import com.liantong.spider.config.SpiderConfig;
import com.liantong.spider.dto.Circle;
import com.liantong.spider.dto.Post;
import com.liantong.spider.queue.SpiderQueue;
import com.liantong.spider.util.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * author:ZhengXing
 * datetime:2018-04-14 15:40
 * 主任务
 */
@Slf4j
@Component
public class MainTask {

    private final HttpClientUtil httpClientUtil;
    private final SpiderQueue spiderQueue;
    private final SpiderConfig spiderConfig;

    /**
     * 获取帖子任务 线程池
     */
    private final ExecutorService getPostExecutorService;

    /**
     * 解析帖子任务 线程池
     */
    private final ExecutorService parsePostExecutorService;

    public MainTask(HttpClientUtil httpClientUtil, SpiderQueue spiderQueue, SpiderConfig spiderConfig) {
        this.httpClientUtil = httpClientUtil;
        this.spiderQueue = spiderQueue;
        this.spiderConfig = spiderConfig;
        this.getPostExecutorService = Executors.newFixedThreadPool(spiderConfig.getService().getGetPostTaskThreadNum());
        this.parsePostExecutorService = Executors.newFixedThreadPool(spiderConfig.getService().getParsePostTaskThreadNum());
    }

    /**
     * 运行方法
     */
    public void run() {
        // 获取圈子任务
        GetCircleTask getCircleTask = new GetCircleTask(httpClientUtil, spiderQueue);
        // 异步运行获取圈子任务
        new Thread(getCircleTask).start();

        // 循环从阻塞队列中获取入库的 圈子, 运行 从圈子获取所有帖子任务
        new Thread(()->{
            while (true){
                try {
                    Circle circle = spiderQueue.takeFromCircleQueue();
                    getPostExecutorService.execute(new GetPostTask(circle.getPath(),circle.getTitle(),httpClientUtil,spiderQueue));
                } catch (Exception e) {
                    log.error("获取帖子任务异常:{}",e);
                }
            }
        }).start();

        // 循环从队列中获取入库的 帖子, 运行 解析帖子任务
        new Thread(()->{
            while (true){
                try {
                    Post post = spiderQueue.takeFromPostQueue();
                    parsePostExecutorService.execute(new ParsePostTask(post.getPath(),post.getTitle(),httpClientUtil));
                } catch (Exception e) {
                    log.error("获取帖子任务异常:{}",e);
                }
            }
        }).start();

    }
}
