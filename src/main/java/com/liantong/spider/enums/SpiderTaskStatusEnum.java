package com.liantong.spider.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * author:ZhengXing
 * datetime:2018-04-14 21:47
 * 爬虫任务状态枚举
 * see {@link com.liantong.spider.entity.SpiderTask#status}
 */
@AllArgsConstructor
@Getter
public enum SpiderTaskStatusEnum implements CodeEnum<Integer>{
    RUNNING(0, "运行中"),
    END(1, "结束"),
    INTERRUPT(2, "中断"),
    ;
    private Integer code;
    private String message;

}
