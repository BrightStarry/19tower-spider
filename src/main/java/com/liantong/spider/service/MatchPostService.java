package com.liantong.spider.service;

import com.liantong.spider.entity.MatchPost;
import com.liantong.spider.vo.PageVO;
import org.springframework.data.domain.Pageable;


/**
 * author:ZhengXing
 * datetime:2018-04-15 11:42
 * 匹配的帖子
 */
public interface MatchPostService {
    PageVO<MatchPost> listMatchPostByPage(Pageable pageable, long spiderTaskId);
}
