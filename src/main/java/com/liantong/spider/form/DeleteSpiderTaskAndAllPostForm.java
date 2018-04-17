package com.liantong.spider.form;

import com.liantong.spider.controller.MainController;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;

/**
 * 中断任务表单
 * see {@link MainController#deleteSpiderTaskAndAllPost()}
 */
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class DeleteSpiderTaskAndAllPostForm {
    /**
     * spiderTask id
     */
    @NotNull(message = "id不能为空")
    private Long spiderTaskId;
}
