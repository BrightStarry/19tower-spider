package com.liantong.spider.form;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

/**
 * author:ZhengXing
 * datetime:2018-04-15 11:20
 * 开启新任务接口 表单
 * see {@link com.liantong.spider.controller.MainController#startSpiderTask(String)}
 */
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class StartSpiderTaskForm {

    @NotBlank(message = "关键词不能为空")
    @Length(min = 1, max = 1024, message = "关键词长度不正确（1-1024")
    private String keyword;
}
