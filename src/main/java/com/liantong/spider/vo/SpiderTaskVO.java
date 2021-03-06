package com.liantong.spider.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

/**
 * author:ZhengXing
 * datetime:2018-04-14 21:40
 * 爬虫任务
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class SpiderTaskVO {

    /**
     * id
     */
    private Long id;

    /**
     * 任务状态
     * see {@link com.liantong.spider.enums.SpiderTaskStatusEnum}
     */
    private String status;

    /**
     * 关键词,用分号分割
     */
    private String keyword;

    /**
     * 任务结束时间
     */
    private Date endTime;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

}
