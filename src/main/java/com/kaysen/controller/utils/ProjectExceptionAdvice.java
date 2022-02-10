package com.kaysen.controller.utils;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.concurrent.TimeoutException;

/**
 * 项目异常处理器，仅对RestController有效
 */
@RestControllerAdvice //配置增强
public class ProjectExceptionAdvice {
    //拦截所有异常
    @ExceptionHandler
    public R doException(Exception ex) {
        ex.printStackTrace();
        if (ex.getClass() == TimeoutException.class) {
            //超时异常处理
            return new R(false, "服务器正忙，稍后再试试吧！");
        } else {
            //其它异常
            return new R(false, "服务器错误，请稍后再试！" + ex.getClass().getSimpleName());
        }
    }
}
