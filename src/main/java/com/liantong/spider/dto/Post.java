package com.liantong.spider.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * author:ZhengXing
 * datetime:2018-04-13 19:25
 * 帖子
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class Post {
    /**
     * 帖子标题
     */
    private String title;

    /**
     * 帖子路径
     */
    private String path;
}
