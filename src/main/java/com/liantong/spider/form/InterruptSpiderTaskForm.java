package com.liantong.spider.form;

import com.liantong.spider.controller.MainController;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.validation.BindingResult;

import javax.validation.constraints.NotNull;

/**
 * 中断任务表单
 * see {@link MainController#interruptSpiderTask(InterruptSpiderTaskForm, BindingResult)}
 */
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class InterruptSpiderTaskForm {

    /**
     * spiderTask id
     */
    @NotNull(message = "id不能为空")
    private Long spiderTaskId;
}
