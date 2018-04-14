package com.liantong.spider.config;

import com.liantong.spider.util.HttpClientUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * author:ZhengXing
 * datetime:2018-04-13 17:57
 * 配置类
 */
@Component
@Data
@AllArgsConstructor
@NoArgsConstructor
@ConfigurationProperties(prefix = "spider")
public class SpiderConfig {


    /**
     * httpClient配置类
     */
    private HttpClientUtil.DefaultHttpClientConfig httpClient = new HttpClientUtil.DefaultHttpClientConfig();

    /**
     * 队列配置
     */
    private QueueConfig queueConfig = new QueueConfig();

    /**
     * 业务配置
     */
    private Service service = new Service();


    /**
     * 队列配置类
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class QueueConfig{
        /**
         * 队列最大长度
         */
        private Integer queueSize = 10240;
    }

    /**
     * 业务配置
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Service{
        /**
         * 要匹配的关键词,用分号分割
         */
        private String keyword = "美女;妹子";

        /**
         * 获取帖子任务线程数
         */
        private Integer getPostTaskThreadNum = 1;

        /**
         * 解析帖子任务线程数
         */
        private Integer parsePostTaskThreadNum = 1;
    }
}
