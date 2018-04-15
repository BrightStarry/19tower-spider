package com.liantong.spider.config;

import com.liantong.spider.entity.SpiderTask;
import com.liantong.spider.enums.SpiderTaskStatusEnum;
import com.liantong.spider.repository.SpiderTaskRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * author:ZhengXing
 * datetime:2018-04-15 13:08
 */
@Component
public class ScheduledService {

    private final SpiderTaskRepository spiderTaskRepository;
    private final SpiderConfig spiderConfig;

    public ScheduledService(SpiderTaskRepository spiderTaskRepository, SpiderConfig spiderConfig) {
        this.spiderTaskRepository = spiderTaskRepository;
        this.spiderConfig = spiderConfig;
    }



    /**
     * 更新失败任务状态
     */
    @Scheduled(cron = "0 */30 * * * ?")
    public void updateErrorSpiderTask() {
        Integer errorTaskTimeoutMs = spiderConfig.getService().getErrorTaskTimeoutMs();
        // 当前所有运行中的任务
        List<SpiderTask> runningTasks = spiderTaskRepository.findByStatus(SpiderTaskStatusEnum.RUNNING.getCode());

        long now = System.currentTimeMillis();


        List<SpiderTask> errorTasks = runningTasks.stream()
                // 过滤出超时任务
                .filter(item -> now - item.getCreateTime().getTime() > errorTaskTimeoutMs)
                // 修改超时数据状态
                .map(item -> item.setStatus(SpiderTaskStatusEnum.ERROR.getCode()))
                .collect(Collectors.toList());
        // 批量修改
        spiderTaskRepository.save(errorTasks);
    }
}
