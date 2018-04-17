package com.liantong.spider.service.impl;

import com.liantong.spider.convertor.JPAPage2PageVOConverter;
import com.liantong.spider.entity.MatchPost;
import com.liantong.spider.repository.MatchPostRepository;
import com.liantong.spider.service.MatchPostService;
import com.liantong.spider.vo.PageVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * author:ZhengXing
 * datetime:2018-04-15 11:42
 * 匹配帖子 服务类
 */
@Service
public class MatchPostServiceImpl implements MatchPostService {

    private final MatchPostRepository matchPostRepository;

    public MatchPostServiceImpl(MatchPostRepository matchPostRepository) {
        this.matchPostRepository = matchPostRepository;
    }

    /**
     * 根据 爬虫任务id 分页查询匹配的帖子
     */
    public PageVO<MatchPost> listMatchPostByPage(Pageable pageable, long spiderTaskId) {
        Page<MatchPost> page = matchPostRepository.findAllBySpiderTaskId(pageable, spiderTaskId);
        return JPAPage2PageVOConverter.convert(page);
    }

    /**
     * 根据爬虫任务id 删除所有匹配的帖子
     */
    @Transactional
    public void deleteAllBySpiderTaskId(long spiderTaskId) {
        matchPostRepository.deleteBySpiderTaskId(spiderTaskId);
    }

}
