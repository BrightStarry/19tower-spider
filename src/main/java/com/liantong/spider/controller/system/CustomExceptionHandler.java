package com.liantong.spider.controller.system;

import com.liantong.spider.enums.ErrorEnum;
import com.liantong.spider.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;

/**
 * author:ZhengXing
 * datetime:2018-03-11 6:36
 * 异常处理类
 */
@ControllerAdvice
@Slf4j
public class CustomExceptionHandler {
    /**
     * 自定义异常处理
     */
    @ExceptionHandler(CustomException.class)
    public String customExceptionHandle(CustomException e, HttpServletRequest request) {
        setCodeAndMessage(request,e.getCode(),e.getMessage());
        return "forward:/error/";
    }

    /**
     * 未受检异常处理
     */
    @ExceptionHandler(Exception.class)
    public String exceptionHandler(Exception e, HttpServletRequest request) {
        log.error("[异常处理]未知异常,error={}",e);
        setCodeAndMessage(request, ErrorEnum.UNKNOWN_ERROR.getCode(),ErrorEnum.UNKNOWN_ERROR.getMessage());
        return "forward:/error/";
    }

    /**
     * 给request设置code和message属性
     */
    public void setCodeAndMessage(HttpServletRequest request, String code, String message) {
        request.setAttribute("code",code);
        request.setAttribute("message",message);
    }
}
