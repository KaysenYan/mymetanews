package com.kaysen.domain.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * 新闻类型枚举
 */
@AllArgsConstructor
@Getter
@ToString
@JsonFormat(shape = JsonFormat.Shape.OBJECT) //将枚举序列化为json对象，方便前端取值
public enum NewsTypeEnum {
    ZONGHE(1, "综合新闻"),
    TOUTIAO(2, "今日头条"),
    YULE(3, "娱乐新闻"),
    GUOJI(4, "国际新闻"),
    IT(5, "IT资讯"),
    QICHE(6, "汽车新闻"),
    LVYOU(7, "旅游资讯"),
    KEJI(8, "科技新闻"),
    JIANKANG(9, "健康知识"),
    YOUXI(10, "游戏资讯");

    @EnumValue //MP的枚举转换注解
    private final int code; //新闻类型数值，数据库存储的是该int值
    private final String typeName; //新闻类型名
}
