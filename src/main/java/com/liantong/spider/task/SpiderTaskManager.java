package com.liantong.spider.task;

import com.liantong.spider.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 爬虫任务管理器
 * 管理 {@link SpiderMainTask}
 */
@Component
@Slf4j
public class SpiderTaskManager {


    /**
     * 当前运行任务列表
     */
    private List<SpiderMainTask> tasks = new CopyOnWriteArrayList<>();

    /**
     * 加入 运行任务列表
     */
    public void add(SpiderMainTask spiderMainTask) {
        tasks.add(spiderMainTask);
    }

    /**
     * 根据id 查询任务
     */
    public SpiderMainTask find(Long spiderTaskId) {
        for (SpiderMainTask task : tasks) {
            if (spiderTaskId.equals(task.getSpiderTaskId())) {
                return task;
            }
        }
        return null;
    }

    /**
     * 根据id,中断并删除某个任务
     * @param isInterrupt true: 中断; false: 结束的任务
     */
    public boolean  interruptAndRemove(Long spiderTaskId,boolean  isInterrupt) {
        SpiderMainTask spiderMainTask = find(spiderTaskId);
        if(spiderMainTask == null){
            log.info("当前任务id:{},不存在任务队列",spiderTaskId);
            return false;
        }
        // 中断任务
        spiderMainTask.interrupt( isInterrupt);
        // 删除任务
        remove(spiderTaskId);
        return true;
    }

    /**
     * 根据id,中断并删除某个任务
     */
    public void interruptAndRemove(Collection<Long> spiderTaskIds,boolean isInterrupt) {
        for (Long spiderTaskId : spiderTaskIds) {
            try {
                interruptAndRemove(spiderTaskId,isInterrupt);
            } catch (Exception e) {
                log.error("任务id:{},中断并删除任务失败:",spiderTaskId,e);
            }
        }
    }

    /**
     * 从运行任务列表删除,根据任务id
     */
    public boolean remove(Long spiderTaskId) {
        return tasks.removeIf(item -> spiderTaskId.equals(item.getSpiderTaskId()));
    }

}
