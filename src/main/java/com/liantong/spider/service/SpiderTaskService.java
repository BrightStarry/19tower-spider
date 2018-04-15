package com.liantong.spider.service;

import com.liantong.spider.entity.SpiderTask;
import com.liantong.spider.enums.SpiderTaskStatusEnum;
import com.liantong.spider.vo.PageVO;
import com.liantong.spider.vo.SpiderTaskVO;
import org.springframework.data.domain.Pageable;

/**
 * author:ZhengXing
 * datetime:2018-04-15 9:34
 * 任务服务
 */
public interface SpiderTaskService {
    int countRunningTask(SpiderTaskStatusEnum spiderTaskStatusEnum);

    void startTask(String keyword);

    PageVO<SpiderTaskVO> listSpiderTaskByPage(Pageable pageable);

    SpiderTaskVO selectSpiderTaskVOById(long spiderTaskId);

}
