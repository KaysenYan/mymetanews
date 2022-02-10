package com.kaysen.domain.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * 性别枚举
 */
@AllArgsConstructor
@Getter
@ToString
@JsonFormat(shape = JsonFormat.Shape.OBJECT) //将枚举序列化为json对象，方便前端取值
public enum GenderEnum {
    SECRET(0, "保密"),
    MALE(1, "男"),
    FEMALE(2, "女");

    @EnumValue //MP的枚举转换注解
    private final int code; //性别数值，数据库存储的是该int值
    private final String gender; //性别类别字符串，方便前端取值
}
