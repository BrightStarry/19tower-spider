package com.liantong.spider.repository;

import com.liantong.spider.entity.MatchPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * author:ZhengXing
 * datetime:2018-04-14 15:28
 * 匹配的帖子 dao
 */
@Repository
public interface MatchPostRepository extends JpaRepository<MatchPost,Long>{
}
