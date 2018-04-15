package com.liantong.spider.repository;

import com.liantong.spider.ApplicationTests;
import com.liantong.spider.entity.SpiderTask;
import com.liantong.spider.enums.SpiderTaskStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * author:ZhengXing
 * datetime:2018-04-15 9:23
 */
@Slf4j
public class SpiderTaskRepositoryTest extends ApplicationTests {

    @Autowired
    private SpiderTaskRepository spiderTaskRepository;

    @Test
    public void testSave() {
        SpiderTask spiderTask = new SpiderTask(SpiderTaskStatusEnum.RUNNING.getCode(), "xxxx");
        spiderTaskRepository.save(spiderTask);
        log.info("{}",spiderTask);

    }

}