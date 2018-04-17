package com.liantong.spider.service.impl;

import com.liantong.spider.config.SpiderConfig;
import com.liantong.spider.convertor.JPAPage2PageVOConverter;
import com.liantong.spider.convertor.SpiderTask2SpiderTaskVOConvertor;
import com.liantong.spider.entity.SpiderTask;
import com.liantong.spider.enums.SpiderTaskStatusEnum;
import com.liantong.spider.exception.CustomException;
import com.liantong.spider.factory.HttpClientUtilFactory;
import com.liantong.spider.repository.SpiderTaskRepository;
import com.liantong.spider.service.MatchPostService;
import com.liantong.spider.service.SpiderTaskService;
import com.liantong.spider.task.SpiderMainTask;
import com.liantong.spider.task.SpiderTaskManager;
import com.liantong.spider.vo.PageVO;
import com.liantong.spider.vo.SpiderTaskVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * author:ZhengXing
 * datetime:2018-04-15 9:35
 * 任务服务
 */
@Service
@Slf4j
public class SpiderTaskServiceImpl implements SpiderTaskService {

    private final SpiderTaskRepository spiderTaskRepository;
    private final SpiderConfig spiderConfig;
    private final HttpClientUtilFactory httpClientUtilFactory;
    private final SpiderTaskManager spiderTaskManager;
    private final MatchPostService matchPostService;



    public SpiderTaskServiceImpl(SpiderTaskRepository spiderTaskRepository, SpiderConfig spiderConfig, HttpClientUtilFactory httpClientUtilFactory, SpiderTaskManager spiderTaskManager, MatchPostService matchPostService) {
        this.spiderTaskRepository = spiderTaskRepository;
        this.spiderConfig = spiderConfig;
        this.httpClientUtilFactory = httpClientUtilFactory;
        this.spiderTaskManager = spiderTaskManager;
        this.matchPostService = matchPostService;
    }

    /**
     * 查询任意状态任务总数
     */
    public int countRunningTask(SpiderTaskStatusEnum spiderTaskStatusEnum) {
        return spiderTaskRepository.countByStatus(spiderTaskStatusEnum.getCode());
    }

    /**
     * 开启新任务
     */

    public void startTask(String keyword) {
        int i = countRunningTask(SpiderTaskStatusEnum.RUNNING);
        // 同时运行的最大任务数
        if (i > spiderConfig.getService().getMaxRunningSpiderTaskNum()) {
            throw new CustomException("正在运行的任务过多,请稍候尝试.当前运行中任务数:" + i);
        }
        // 创建主任务
        SpiderMainTask spiderMainTask = new SpiderMainTask( spiderConfig, spiderTaskRepository, httpClientUtilFactory, spiderTaskManager, keyword);
        // 在主任务中同步开启其他任务
        spiderMainTask.run();
    }

    /**
     * 分页查询所有任务
     */
    public PageVO<SpiderTaskVO> listSpiderTaskByPage(Pageable pageable) {
        Page<SpiderTask> page = spiderTaskRepository.findAll(pageable);
        return SpiderTask2SpiderTaskVOConvertor.convert(JPAPage2PageVOConverter.convert(page));
    }

    /**
     * 根据id查询任务VO
     */
    public SpiderTaskVO selectSpiderTaskVOById(long spiderTaskId) {
        SpiderTask spiderTask = spiderTaskRepository.findOne(spiderTaskId);
        if(spiderTask == null)
            throw new CustomException("该任务不存在");
        return SpiderTask2SpiderTaskVOConvertor.convert(spiderTask);
    }

    /**
     * 根据id中断任务
     */
    @Transactional
    public void interruptSpiderTask(Long spiderTaskId) {
        SpiderTask spiderTask = spiderTaskRepository.findOne(spiderTaskId);
        if (spiderTask == null) {
            throw new CustomException("该任务不存在");
        }

        // 从任务管理中删除并中断该任务
        boolean flag = spiderTaskManager.interruptAndRemove(spiderTaskId,true);
        // 如果返回为空,表示该任务不存在, 通常为上次启动意外终止的遗留记录
        if (!flag) {
            spiderTaskRepository.save(spiderTask.setStatus(SpiderTaskStatusEnum.INTERRUPT.getCode()));
        }
    }

    /**
     * 删除任务 和 其下所有记录
     */
    @Transactional
    public void deleteOne(long spiderTaskId) {
        if(!spiderTaskRepository.exists(spiderTaskId))
            throw new CustomException("该任务不存在");
        spiderTaskRepository.delete(spiderTaskId);
        matchPostService.deleteAllBySpiderTaskId(spiderTaskId);
    }
}
