package com.liantong.spider.controller;

import com.liantong.spider.enums.ErrorEnum;
import com.liantong.spider.exception.CustomException;
import com.liantong.spider.factory.PageRequestFactory;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.BindingResult;

/**
 * author:ZhengXing
 * datetime:2018-04-15 10:35
 * 控制层扩展
 */
public interface ControllerPlus {

    /**
     * 构建{@link Pageable}对象
     */
    default Pageable buildPageable(int pageNo, Integer pageSize) {
        return PageRequestFactory.buildForCommon(pageNo, pageSize);
    }

    /**
     * 表单验证
     * @param bindingResult 检验返回对象
     * @param log slf4j日志对象
     * @param logInfo 日志内容
     * @param logObject 日志输出对象
     */
    default void isValid(BindingResult bindingResult, Logger log, String logInfo, Object... logObject) {
        //如果校验不通过,记录日志，并抛出异常
        if (bindingResult.hasErrors()) {
            if(StringUtils.isNotBlank(logInfo))
                log.error(logInfo, logObject);
            throw new CustomException(ErrorEnum.FORM_ERROR.getCode(), bindingResult.getFieldError().getDefaultMessage());
        }
    }

    /**
     * 表单验证
     */
    default   void isValid(BindingResult bindingResult) {
        isValid(bindingResult,null,null);
    }
}
