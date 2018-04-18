package com.liantong.spider.task;

import com.liantong.spider.config.SpiderConfig;
import com.liantong.spider.dto.Circle;
import com.liantong.spider.dto.Post;
import com.liantong.spider.entity.SpiderTask;
import com.liantong.spider.enums.SpiderTaskStatusEnum;
import com.liantong.spider.factory.HttpClientUtilFactory;
import com.liantong.spider.queue.SpiderQueue;
import com.liantong.spider.repository.SpiderTaskRepository;
import com.liantong.spider.util.HttpClientUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * author:ZhengXing
 * datetime:2018-04-14 15:40
 * 爬虫主任务
 */
@Slf4j
public class SpiderMainTask {

    private final SpiderQueue spiderQueue;
    private final SpiderTaskRepository  spiderTaskRepository;
    private final HttpClientUtilFactory httpClientUtilFactory;
    private final SpiderTaskManager spiderTaskManager;
    private final SpiderConfig spiderConfig;

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

    /**
     * 当前任务id
     */
    @Getter  private Long spiderTaskId;

    /**
     * 当前任务记录
     */
    private SpiderTask spiderTask;

    /**
     * 中断任务标识
     */
    public volatile Boolean isInterrupt = false;

    /**
     * httpClient连接池
     */
    private  HttpClientUtil httpClientUtil;





    public SpiderMainTask(SpiderConfig spiderConfig, SpiderTaskRepository spiderTaskRepository, HttpClientUtilFactory httpClientUtilFactory, SpiderTaskManager spiderTaskManager, String keyword) {
        this.spiderConfig = spiderConfig;
        this.spiderQueue = new SpiderQueue(spiderConfig);
        this.getPostExecutorService = Executors.newFixedThreadPool(spiderConfig.getService().getGetPostTaskThreadNum());
        this.parsePostExecutorService = Executors.newFixedThreadPool(spiderConfig.getService().getParsePostTaskThreadNum());
        this.spiderTaskRepository = spiderTaskRepository;
        this.httpClientUtilFactory = httpClientUtilFactory;
        this.spiderTaskManager = spiderTaskManager;
        this.keyword = keyword;
    }

    /**
     * 运行方法
     */
    public void run() {
        // 创建任务
        spiderTask = new SpiderTask(SpiderTaskStatusEnum.RUNNING.getCode(), keyword).setCreateTime(new Date());
        // 入库
        spiderTaskRepository.save(spiderTask);
        // 设置任务id
        this.spiderTaskId = spiderTask.getId();
        spiderTask = spiderTaskRepository.findOne(spiderTaskId);

        // 放入任务管理器
        spiderTaskManager.add(this);

        log.info("准备启动新任务,当前任务信息:{}",spiderTask);


        // 创建新的http线程池
        this.httpClientUtil = httpClientUtilFactory.buildDefaultHttpClientUtil();

        // 关键词数组
        final String[] keywordArray = keyword.split(";");

        // 获取圈子任务
        GetCircleTask getCircleTask = new GetCircleTask(httpClientUtil, spiderQueue,this, spiderTaskId);
        // 异步运行获取圈子任务
        new Thread(getCircleTask).start();

        // 循环从阻塞队列中获取入库的 圈子, 运行 从圈子获取所有帖子任务
        new Thread(()->{
            while (true){
                try {
                    Circle circle = spiderQueue.takeFromCircleQueue();
                    // 如果是执行完毕
                    if (circle == null || isInterrupt) {
                        log.info("当前任务id:{},获取帖子任务结束",spiderTask.getId());
                        // 关闭线程池, 并等待线程执行完毕,直到超时
                        getPostExecutorService.shutdown();
                        getPostExecutorService.awaitTermination(spiderConfig.getService().getErrorTaskTimeoutMinute(), TimeUnit.MINUTES);
                        return;
                    }
                    getPostExecutorService.execute(new GetPostTask(circle.getPath(),circle.getTitle(),httpClientUtil,spiderQueue, this));
                } catch (Exception e) {
                    log.error("当前任务id:{},获取帖子任务异常:{}",spiderTask.getId(),e);
                }
            }
        }).start();

        // 循环从队列中获取入库的 帖子, 运行 解析帖子任务
        new Thread(()->{
            while (true){
                try {
                    Post post = spiderQueue.takeFromPostQueue();
                    // 如果是执行完毕
                    if (post == null ) {
                        // 等待线程执行完毕
                        parsePostExecutorService.shutdown();
                        parsePostExecutorService.awaitTermination(spiderConfig.getService().getErrorTaskTimeoutMinute(), TimeUnit.MINUTES);
                        return;
                    }
                    // 如果是中断
                    if (isInterrupt) {
                        // 停止任务
                        spiderTaskManager.interruptAndRemove(spiderTaskId,isInterrupt);
                    }

                    parsePostExecutorService.execute(new ParsePostTask(
                            post.getPath(),
                            post.getTitle(),
                            httpClientUtil,
                            spiderTask.getId(),
                            keywordArray,
                            this));
                } catch (Exception e) {
                    log.error("当前任务id:{},获取帖子任务异常:",spiderTask.getId(),e);
                }
            }
        }).start();

    }

    /**
     * 中断任务
     * 此处存在一个需要注意的地方, {@link this#spiderTask} 有NPE的可能性, 但应该不会发生
     * 因为正常来说,执行{@link this#run()}方法后,spiderTask就会创建,不会为null,除非在这间隙调用该方法
     */
    public void interrupt(boolean isInterrupt) {
        log.info("当前任务id:{},解析帖子任务结束",spiderTask.getId());
        // 设置标识为中断
        this.isInterrupt = true;
        // 关闭线程池,只关闭这一个,另一个已经在之前关闭了
        parsePostExecutorService.shutdown();
        // 关闭http连接池
        httpClientUtil.shutdown();
        // 该任务结束表示总任务结束,修改任务状态为结束,并记录结束时间
        spiderTaskRepository.save(spiderTask
                .setStatus(isInterrupt ? SpiderTaskStatusEnum.INTERRUPT.getCode() : SpiderTaskStatusEnum.END.getCode())
                .setEndTime(new Date()));
        log.info("当前任务id:{},任务结束,当前任务信息:{}",spiderTask.getId(),spiderTask);
    }
}
