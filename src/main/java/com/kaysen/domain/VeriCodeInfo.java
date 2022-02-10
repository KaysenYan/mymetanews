package com.kaysen.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 验证码信息
 */
@Data
@TableName("tb_veriCodeInfo")
public class VeriCodeInfo {
    @TableId
    private Integer id;  //主键 自增
    private String email; //注册邮箱
    private String phone; //注册手机号
    private String veriCode; //验证码
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date veriCodeTime; //该验证码起始时间
}
