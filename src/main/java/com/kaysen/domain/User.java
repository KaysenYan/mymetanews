package com.kaysen.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.Version;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.kaysen.domain.enums.GenderEnum;
import com.kaysen.domain.enums.RoleEnum;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 用户信息
 */
@Data
public class User {
    @TableId
    private Integer id; //主键 自增
    private String username; //用户名
    @TableField(select = false) //禁止查询密码
    private String password; //密码
    private String phone;    //手机号
    private String email;    //邮箱
    private GenderEnum gender; //性别，0保密，1男，2女
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date birthday; //生日
    private String introduction; //个性签名
    private RoleEnum role;    //角色，1管理员，2普通用户
    @Version
    private Integer version; //mybatis-plus 乐观锁版本号
    @TableLogic
    private Integer deleted; //是否删除(逻辑删除，禁用)，未删除0，已删除1
}