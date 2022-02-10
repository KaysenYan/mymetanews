package com.kaysen.domain;

import lombok.Data;

import java.util.List;

/**
 * 用于封装前端传来的未保存的新闻数据
 */
@Data
public class UnsaveData {
    private List<News> newslist;
}
