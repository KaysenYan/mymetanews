package com.kaysen.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.kaysen.domain.enums.NewsTypeEnum;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 新闻条目数据
 */
@Data
public class News {
    @TableId
    private String id;     //新闻主键
    private NewsTypeEnum type;   //新闻类型
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date ctime;   //时间
    private String title;   //标题
    private String description; //描述
    private String source; //来源
    private String picUrl; //图片链接
    private String url;    //正文链接
}
