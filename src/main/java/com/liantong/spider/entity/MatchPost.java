package com.liantong.spider.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * author:ZhengXing
 * datetime:2018-04-14 15:21
 * 与关键词匹配的帖子
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class MatchPost {

    /**
     * id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 帖子标题
     */
    private String title;

    /**
     * uri
     */
    private String uri;

    /**
     * 内容
     */
    private String content;

    /**
     * 匹配的关键词
     */
    private String keyword;

    public MatchPost(String title, String uri, String content,String keyword) {
        this.title = title;
        this.uri = uri;
        this.content = content;
        this.keyword = keyword;
    }
}
