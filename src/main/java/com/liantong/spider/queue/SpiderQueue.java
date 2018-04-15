package com.liantong.spider.queue;

import com.liantong.spider.config.SpiderConfig;
import com.liantong.spider.dto.Circle;
import com.liantong.spider.dto.Post;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * author:ZhengXing
 * datetime:2018-04-13 19:09
 * 爬虫队列
 */
public class SpiderQueue {

    /**
     * 圈子队列
     * 存储 {@link com.liantong.spider.dto.Circle}
     */
    private final BlockingQueue<Circle> circleQueue;

    /**
     * 帖子队列
     * 存储 {@link com.liantong.spider.dto.Post}
     */
    private final BlockingQueue<Post> postQueue;

    /**
     * 超时时间
     */
    private final Integer takeTimeoutSecond;

    public SpiderQueue(SpiderConfig config) {
        circleQueue = new LinkedBlockingQueue<>(config.getQueueConfig().getQueueSize());
        postQueue = new LinkedBlockingQueue<>(config.getQueueConfig().getQueueSize());
        takeTimeoutSecond = config.getQueueConfig().getTakeTimeoutSecond();
    }

    /**
     * 圈子队列  入队
     */
    @SneakyThrows
    public void putToCircleQueue(Circle circle) {
        circleQueue.put(circle);
    }

    /**
     * 圈子队列  出队
     */
    @SneakyThrows
    public Circle takeFromCircleQueue() {
        return circleQueue.poll(takeTimeoutSecond, TimeUnit.SECONDS);
    }

    /**
     * 帖子队列  入队
     */
    @SneakyThrows
    public void putToPostQueue(Post post) {
        postQueue.put(post);
    }

    /**
     * 帖子队列  出队
     */
    @SneakyThrows
    public Post takeFromPostQueue() {
        return postQueue.poll(takeTimeoutSecond, TimeUnit.SECONDS);
    }
}
