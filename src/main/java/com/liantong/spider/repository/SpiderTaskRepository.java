package com.liantong.spider.repository;

import com.liantong.spider.entity.MatchPost;
import com.liantong.spider.entity.SpiderTask;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * author:ZhengXing
 * datetime:2018-04-14 15:28
 * 爬虫任务 dao
 */
@Repository
public interface SpiderTaskRepository extends JpaRepository<SpiderTask,Long>{

    /**
     * 根据状态查询记录数
     */
    int countByStatus(int status);

    /**
     * 根据状态查询所有记录
     */
    List<SpiderTask> findByStatus(int status);


}
