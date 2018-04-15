package com.liantong.spider.exception;

import com.liantong.spider.enums.CodeEnum;
import com.liantong.spider.enums.ErrorEnum;
import lombok.Getter;

/**
 * author:ZhengXing
 * datetime:2017/11/7 0007 16:40
 * 自定义异常
 */
@Getter
public class CustomException extends RuntimeException {
    private String code = ErrorEnum.COMMON_ERROR.getCode();

    /**
     * 根据异常枚举构造自定义异常
     * @param codeEnum
     */
    public CustomException(CodeEnum<String> codeEnum){
        super(codeEnum.getMessage());
        this.code = codeEnum.getCode();
    }

    /**
     * 根据异常码和消息构造自定义异常
     * @param code
     * @param message
     */
    public CustomException(String code, String message) {
        super(message);
        this.code = code;
    }

    /**
     * 根据消息构造自定义异常
     * @param message
     */
    public CustomException(String message) {
        super(message);
    }
}
