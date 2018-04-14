package com.liantong.spider.queue;

import com.liantong.spider.config.SpiderConfig;
import com.liantong.spider.dto.Circle;
import com.liantong.spider.dto.Post;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * author:ZhengXing
 * datetime:2018-04-13 19:09
 * 爬虫队列
 */
@Component
public class SpiderQueue {

    /**
     * 圈子队列
     * 存储 {@link com.liantong.spider.dto.Circle}
     */
    private BlockingQueue<Circle> circleQueue;

    /**
     * 帖子队列
     * 存储 {@link com.liantong.spider.dto.Post}
     */
    private BlockingQueue<Post> postQueue;

    public SpiderQueue(SpiderConfig config) {
        circleQueue = new LinkedBlockingQueue<>(config.getQueueConfig().getQueueSize());
        postQueue = new LinkedBlockingQueue<>(config.getQueueConfig().getQueueSize());
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
        return circleQueue.take();
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
        return postQueue.take();
    }
}
