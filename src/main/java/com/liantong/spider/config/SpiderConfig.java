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

        /**
         * 队列获取超时时间,超时后,默认该任务已经结束
         */
        private Integer takeTimeoutSecond = 60;
    }

    /**
     * 业务配置
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Service{
        /**
         * 获取帖子任务线程数
         */
        private Integer getPostTaskThreadNum = 1;

        /**
         * 解析帖子任务线程数
         */
        private Integer parsePostTaskThreadNum = 1;

        /**
         * 最多同时运行 {@link com.liantong.spider.entity.SpiderTask} 任务个数
         */
        private Integer maxRunningSpiderTaskNum = 3;

        /**
         * 分页查询,默认每天记录数
         */
        private Integer defaultPageSize = 10;

        /**
         * 任务超过多少时间还未结束,结束该任务. 分钟
         */
        private Integer errorTaskTimeoutMinute = 300;

    }
}
