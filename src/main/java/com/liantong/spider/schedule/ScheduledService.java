package com.liantong.spider.schedule;

import com.liantong.spider.config.SpiderConfig;
import com.liantong.spider.entity.SpiderTask;
import com.liantong.spider.enums.SpiderTaskStatusEnum;
import com.liantong.spider.repository.SpiderTaskRepository;
import com.liantong.spider.task.SpiderTaskManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * author:ZhengXing
 * datetime:2018-04-15 13:08
 * 定时任务
 */
@Component
@Slf4j
public class ScheduledService {

    private final SpiderTaskRepository spiderTaskRepository;
    private final SpiderConfig spiderConfig;
    private final SpiderTaskManager spiderTaskManager;

    public ScheduledService(SpiderTaskRepository spiderTaskRepository, SpiderConfig spiderConfig, SpiderTaskManager spiderTaskManager) {
        this.spiderTaskRepository = spiderTaskRepository;
        this.spiderConfig = spiderConfig;
        this.spiderTaskManager = spiderTaskManager;
    }



    /**
     * 更新失败任务状态
     */
    @Scheduled(cron = "0 */10 * * * ?")
    public void updateErrorSpiderTask() {
        log.info("[定时任务]开始执行");
        // 当前所有运行中的任务
        List<SpiderTask> runningTasks = spiderTaskRepository.findByStatus(SpiderTaskStatusEnum.RUNNING.getCode());
        // 为空退出
        if(CollectionUtils.isEmpty(runningTasks))
            return;

        // 超时时间
        Integer errorTaskTimeoutMinute = spiderConfig.getService().getErrorTaskTimeoutMinute();
        // 超时时间 分钟 转 毫秒数
        long errorTaskTimeoutMs =  TimeUnit.MILLISECONDS.convert(errorTaskTimeoutMinute, TimeUnit.MINUTES);

        // 当前时间
        long now = System.currentTimeMillis();

        List<SpiderTask> errorTasks = runningTasks.stream()
                // 过滤出超时任务
                .filter(item -> now - item.getCreateTime().getTime() > errorTaskTimeoutMs)
                // 修改超时数据状态
                .map(item -> item.setStatus(SpiderTaskStatusEnum.INTERRUPT.getCode()))
                .collect(Collectors.toList());
        log.info("[定时任务]当前结束任务数:{}",errorTasks.size());

        // 从任务管理器,中断并删除该任务
        spiderTaskManager.interruptAndRemove(errorTasks.stream().map(SpiderTask::getId).collect(Collectors.toList()),true);

        // 批量修改
        spiderTaskRepository.save(errorTasks);
    }

}
