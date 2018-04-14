package com.liantong.spider.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * author:ZhengXing
 * datetime:2018-04-13 19:03
 * 圈子 对象
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class Circle {

    /**
     * 名字
     */
    private String title;

    /**
     * 路径
     */
    private String path;
}
