package com.kaysen.domain.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * 用户角色身份枚举
 */
@AllArgsConstructor
@Getter
@ToString
@JsonFormat(shape = JsonFormat.Shape.OBJECT) //将枚举序列化为json对象，方便前端取值
public enum RoleEnum {
    ADMIN(1, "管理员"),
    USER(2, "普通用户");

    @EnumValue //MP的枚举转换注解
    private final int code; //角色数值，数据库存储的是该int值
    private final String roleName; //角色名

}
