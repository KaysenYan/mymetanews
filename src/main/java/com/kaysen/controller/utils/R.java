package com.kaysen.controller.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统一响应的json数据格式
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class R {
    private Boolean flag; //请求是否成功
    private String msg;   //给浏览器返回的消息
    private Object data;  //数据

    /**
     * 无消息的响应
     */
    public R(Boolean flag, Object data) {
        this.flag = flag;
        this.data = data;
    }

    /**
     * 无数据的响应
     */
    public R(Boolean flag, String msg) {
        this.flag = flag;
        this.msg = msg;
    }
}
