package com.kaysen.domain;

import lombok.Data;

/**
 * 新用户注册，封装注册信息
 */
@Data
public class NewUser {
    private String email;    //邮箱
    private String phone;    //手机号
    private String username; //用户名
    private String password; //密码
    private String veriCode; //验证码
}
