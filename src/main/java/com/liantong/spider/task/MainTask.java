package com.liantong.spider.task;

import com.liantong.spider.config.SpiderConfig;
import com.liantong.spider.dto.Circle;
import com.liantong.spider.dto.Post;
import com.liantong.spider.entity.SpiderTask;
import com.liantong.spider.enums.SpiderTaskStatusEnum;
import com.liantong.spider.queue.SpiderQueue;
import com.liantong.spider.repository.SpiderTaskRepository;
import com.liantong.spider.util.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * author:ZhengXing
 * datetime:2018-04-14 15:40
 * 主任务
 */
@Slf4j
public class MainTask {

    private final HttpClientUtil httpClientUtil;
    private final SpiderQueue spiderQueue;
    private final SpiderTaskRepository spiderTaskRepository;

    /**
     * 获取帖子任务 线程池
     */
    private final ExecutorService getPostExecutorService;

    /**
     * 解析帖子任务 线程池
     */
    private final ExecutorService parsePostExecutorService;

    /**
     * 当前任务关键词
     */
    private final String keyword;

    public MainTask(HttpClientUtil httpClientUtil, SpiderConfig spiderConfig, SpiderTaskRepository spiderTaskRepository,String keyword) {
        this.httpClientUtil = httpClientUtil;
        this.spiderQueue = new SpiderQueue(spiderConfig);
        this.getPostExecutorService = Executors.newFixedThreadPool(spiderConfig.getService().getGetPostTaskThreadNum());
        this.parsePostExecutorService = Executors.newFixedThreadPool(spiderConfig.getService().getParsePostTaskThreadNum());
        this.spiderTaskRepository = spiderTaskRepository;
        this.keyword = keyword;
    }

    /**
     * 运行方法
     */
    public void run() {
        // 创建任务
        SpiderTask spiderTask = new SpiderTask(SpiderTaskStatusEnum.RUNNING.getCode(), keyword);
        // id会自动回写
        spiderTaskRepository.save(spiderTask);

        log.info("准备启动新任务,当前任务信息:{}",spiderTask);

        // 关键词数组
        final String[] keywordArray = keyword.split(";");

        // 获取圈子任务
        GetCircleTask getCircleTask = new GetCircleTask(httpClientUtil, spiderQueue);
        // 异步运行获取圈子任务
        new Thread(getCircleTask).start();

        // 循环从阻塞队列中获取入库的 圈子, 运行 从圈子获取所有帖子任务
        new Thread(()->{
            while (true){
                try {
                    Circle circle = spiderQueue.takeFromCircleQueue();
                    if (circle == null) {
                        log.info("获取帖子任务结束");
                        getPostExecutorService.shutdown();
                        return;
                    }
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
                    if (post == null) {
                        log.info("解析帖子任务结束");
                        parsePostExecutorService.shutdown();
                        // 该任务结束表示总任务结束,修改任务状态为结束,并记录结束时间
                        spiderTaskRepository.save(spiderTask.setStatus(SpiderTaskStatusEnum.END.getCode()).setEndTime(new Date()));
                        log.info("任务结束,当前任务信息:{}",spiderTask);
                        return;
                    }
                    parsePostExecutorService.execute(new ParsePostTask(
                            post.getPath(),
                            post.getTitle(),
                            httpClientUtil,
                            spiderTask.getId(),
                            keywordArray
                            ));
                } catch (Exception e) {
                    log.error("获取帖子任务异常:{}",e);
                }
            }
        }).start();

    }
}
